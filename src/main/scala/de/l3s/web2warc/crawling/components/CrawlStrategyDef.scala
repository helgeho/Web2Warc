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

package de.l3s.web2warc.crawling.components

import de.l3s.web2warc.utils._
import de.l3s.web2warc.warc.Capture

class CrawlStrategyDef extends CrawlStrategy {
  var canonicalUrl: (String) => String = (url) => SURT.fromUrl(url)
  var resourceId: (String, HttpResponse) => String = (url, response) => canonicalUrl.apply(url)
  var getUrls: (HttpResponse) => Iterable[String] = (response) => {
    if (response.mime.getOrElse("").toLowerCase == "text/html") {
      response.stringContent.map(str => Html(str).getAttributes("a", "href").map(a => a.getValue)).getOrElse(Seq())
    } else Seq()
  }
  var request: (String, Map[String, String]) => Array[Byte] = (url, headers) => HttpClientReader.get(url, HttpClientReader.defaultHeaders ++ headers)
  var validUrl: (String) => Boolean = (url) => true
  var validResource: (HttpResponse) => Boolean = (response) => true
  var preserveCapture: (Capture) => Boolean = (capture) => true
  var increaseLevel: (String, String) => Boolean = (srcUrl, dstUrl) => true

  def canonicalUrl(url: String): String = canonicalUrl.apply(url)
  def resourceId(url: String, response: HttpResponse): String = resourceId.apply(url, response)
  def getUrls(response: HttpResponse): Iterable[String] = getUrls.apply(response)
  def request(url: String, headers: Map[String, String]) = request.apply(url, headers)
  def validUrl(url: String): Boolean = validUrl.apply(url)
  def validResource(response: HttpResponse): Boolean = validResource.apply(response)
  def preserveCapture(capture: Capture): Boolean = preserveCapture.apply(capture)
  def increaseLevel(srcUrl: String, dstUrl: String): Boolean = increaseLevel.apply(srcUrl, dstUrl)
}
