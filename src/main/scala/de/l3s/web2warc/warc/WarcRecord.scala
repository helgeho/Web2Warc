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

package de.l3s.web2warc.warc

import java.time.format.DateTimeFormatter

import de.l3s.web2warc.utils.GZip
import org.apache.commons.codec.digest.DigestUtils

case class WarcRecord(header: Array[Byte], capture: Capture, offset: Long, filename: String) {
  val CdxTimeFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
  val CdxNotAvailableStr = "-"
  val CdxDelimiter = " "
  val CdxDelimiterReplacement = "_"

  lazy val bytes = GZip.compress(header ++ capture.response.bytes)
  def length = bytes.length

  def toCDX: String = toCDX()
  def toCDX(meta: String = CdxNotAvailableStr) = {
    val line = StringBuilder.newBuilder
    line ++= capture.surtUrl ++= CdxDelimiter
    line ++= capture.timestamp.format(CdxTimeFormat) ++= CdxDelimiter
    line ++= encodeUrl(capture.url) ++= CdxDelimiter
    line ++= capture.response.mime.map(m => m.replace(CdxDelimiter, CdxDelimiterReplacement)).getOrElse(CdxNotAvailableStr) ++= CdxDelimiter
    line ++= capture.response.status.map(s => s.toString).getOrElse(CdxNotAvailableStr) ++= CdxDelimiter
    line ++= DigestUtils.sha1Hex(capture.response.bytes) ++= CdxDelimiter
    line ++= capture.response.redirectLocation.map(url => encodeUrl(url)).getOrElse(CdxNotAvailableStr) ++= CdxDelimiter
    line ++= meta.replace(CdxDelimiter, CdxDelimiterReplacement) ++= CdxDelimiter
    line ++= length.toString ++= CdxDelimiter
    line ++= offset.toString ++= CdxDelimiter
    line ++= filename.replace(CdxDelimiter, CdxDelimiterReplacement)
    line.toString()
  }

  private def encodeUrl(url: String) = url.replace(" ", "%20")
}
