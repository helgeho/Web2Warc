/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Helge Holzmann
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.l3s.web2warc.crawling

import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import de.l3s.web2warc.crawling.components.io.CrawlWriter
import de.l3s.web2warc.crawling.components.{CrawlInfo, CrawlRunInfo, CrawlSpecification, CrawlStrategy}
import de.l3s.web2warc.utils._
import de.l3s.web2warc.warc.Capture

import scala.collection.immutable.Queue
import scala.util.Try

class Crawler(val seeds: Set[String], val strategy: CrawlStrategy, val spec: CrawlSpecification, val writer: CrawlWriter) {
  private var queuedUrls = Set[String]()

  private var _queue = Queue[QueuedUrl](seeds.map(url => QueuedUrl(url)).toSeq: _*)
  def queue = _queue

  private var _doneUrls = Set[String]()
  def doneUrls = _doneUrls

  private var _doneResources = Set[String]()
  def doneResources = _doneResources

  def run(info: CrawlInfo) = {
    val run = newRunInfo(info)
    writer.init(run)

    while (queue.nonEmpty) {
      val (next, queue) = _queue.dequeue
      _queue = queue

      val url = next.url
      val canonicalUrl = strategy.canonicalUrl(url)
      queuedUrls -= canonicalUrl
      _doneUrls += canonicalUrl

      val timestamp = LocalDateTime.now()
      val bytes = Try {strategy.request(url, spec.httpRequestHeaders)}
      if (bytes.isSuccess) {
        val response = HttpResponse(bytes.get)
        if (strategy.validResource(response)) {
          val resourceId = strategy.resourceId(url, response)
          if (!_doneResources.contains(resourceId)) {
            _doneResources += resourceId

            val capture = Capture(url, timestamp, response)
            if (preserveCapture(capture)) writer.save(capture)

            val currentLevel = next.level
            val nextLevel = currentLevel + 1

            // maxLevel -1 should disable limit and 0 should crawl only one document
            if (nextLevel <= spec.maxLevel || spec.maxLevel < 0) {
              if (spec.followRedirects) {
                for (redirectUrl <- response.header.redirectLocation) enqueue(url, redirectUrl, currentLevel, redirect = true)
              }
              for (nextUrl <- strategy.getUrls(response)) enqueue(url, nextUrl, currentLevel, redirect = false)
            }
          }
        }
      }

      logQueueState()
    }

    writer.close()
  }

  def logQueueState() = {
    val prefix = "::Queue state:: "
    if (queue.isEmpty) println(prefix + "queue is empty.")
    else println(prefix + "current level: " + _queue.head.level + ", URLs in queue: " + _queue.size)
  }

  private def newRunInfo(info: CrawlInfo) = {
    val timestamp = LocalDateTime.now()
    val name = info.name.replaceAll("[^A-z0-9\\-]", "_") + "-" + timestamp.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    CrawlRunInfo(name, timestamp, info, this)
  }

  private def enqueue(baseUrl: String, url: String, currentLevel: Int, redirect: Boolean) = {
    val fullUrl = Try{new URL(new URL(baseUrl), url).toString}.getOrElse(url)
    if (validUrl(fullUrl)) {
      val canonicalUrl = strategy.canonicalUrl(fullUrl)
      if (!queuedUrls.contains(canonicalUrl) && !doneUrls.contains(canonicalUrl)) {
        val increaseLevel = (!redirect || spec.increaseLevelOnRedirect) && strategy.increaseLevel(baseUrl, fullUrl)
        _queue = _queue.enqueue(QueuedUrl(fullUrl, if (increaseLevel) currentLevel + 1 else currentLevel))
        queuedUrls += canonicalUrl
      }
    }
  }

  private def validUrl(url: String) = url.matches(spec.urlRegex) && strategy.validUrl(url)
  private def preserveCapture(capture: Capture) = capture.url.matches(spec.preserveUrlRegex) && strategy.preserveCapture(capture)
}
