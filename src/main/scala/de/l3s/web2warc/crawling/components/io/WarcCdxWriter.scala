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

package de.l3s.web2warc.crawling.components.io

import java.nio.file.Paths
import de.l3s.web2warc.crawling.components.CrawlRunInfo
import de.l3s.web2warc.utils.{GZip, OutFile}
import de.l3s.web2warc.warc.{Capture, WarcHeaders, WarcRecord}

class WarcCdxWriter extends CrawlWriter {
  var path = ""

  private var _run: CrawlRunInfo = null
  def run = _run

  private var warcOffset = 0

  private var _warcFile: OutFile = null
  def warcFile: OutFile = {
    if (_warcFile == null) {
      val warcPath = Paths.get(path, run.name, run.name + ".warc.gz")
      _warcFile = new OutFile(warcPath, true)

      val fileHeader = GZip.compress(WarcHeaders.file(run.info.publisher, run.timestamp, warcPath.getFileName.toString))
      warcOffset = fileHeader.length
      _warcFile.out.write(fileHeader)
    }
    _warcFile
  }

  private var _cdxFile: OutFile = null
  def cdxFile: OutFile = {
    if (_cdxFile == null) {
      val cdxPath = Paths.get(path, run.name, run.name + ".cdx.gz")
      _cdxFile = new OutFile(cdxPath, true)
      _cdxFile.outStream = _cdxFile.gzip
    }
    _cdxFile
  }

  def init(run: CrawlRunInfo) = {
    _run = run
  }

  def save(capture: Capture): Unit = {
    val header = WarcHeaders.responseRecord(capture)
    val warc = warcFile

    val record = WarcRecord(header, capture, warcOffset, warc.path.getFileName.toString)
    warc.out.write(record.bytes)
    warcOffset += record.bytes.length

    cdxFile.print.println(record.toCDX)
  }

  def close() = {
    if (_warcFile != null) _warcFile.close()
    if (_cdxFile != null) _cdxFile.close()
  }
}
