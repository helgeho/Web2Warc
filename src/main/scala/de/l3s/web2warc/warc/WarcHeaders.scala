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

import java.nio.charset.Charset
import java.time._
import java.util.UUID

import de.l3s.web2warc.Constants
import org.apache.commons.codec.digest.DigestUtils


object WarcHeaders {
    val UTF8 = Charset.forName("UTF-8")
    
    private val Br = "\r\n"

    def file(publisher: String, createTime: LocalDateTime, filename: String): Array[Byte] = {
        val header = StringBuilder.newBuilder
        header.append("WARC/1.0").append(Br)
        header.append("WARC-Type: warcinfo").append(Br)
        header.append("WARC-Date: " + LocalDateTime.now + "Z").append(Br)
        header.append("WARC-Filename: " + filename).append(Br)
        header.append("WARC-Record-ID: <" + warcRecordID() +">").append(Br)
        header.append("Content-Type: application/warc-fields").append(Br)

        val headerBody = StringBuilder.newBuilder
        headerBody.append("software: " + Constants.softwareName).append(Br)
        headerBody.append("format: WARC File Format 1.0").append(Br)
        headerBody.append("conformsTo: http://bibnum.bnf.fr/WARC/WARC_ISO_28500_version1_latestdraft.pdf").append(Br)
        headerBody.append("publisher: " + publisher).append(Br)
        headerBody.append("created: " + createTime + "Z").append(Br)
        headerBody.append(Br * 3)

        val headerBodyStr = headerBody.toString()
        val headerBodyBlob = headerBodyStr.getBytes(UTF8)

        header.append("Content-Length: " + headerBodyBlob.length).append(Br)
        header.append(Br)
        header.append(headerBodyStr)

        header.toString().getBytes(UTF8)
    }

    def responseRecord(capture: Capture) = {
        val bytes = capture.response.bytes
        
        val header = StringBuilder.newBuilder
        header.append("WARC/1.0").append(Br)
        header.append("WARC-Type: response").append(Br)
        header.append("WARC-Target-URI: " + capture.url).append(Br)
        header.append("WARC-Date: " + capture.timestamp + "Z").append(Br)
        header.append("WARC-Payload-Digest: sha1:" + DigestUtils.sha1Hex(bytes)).append(Br)
        header.append("WARC-IP-Address: " + capture.response.header.ip.getOrElse("-")).append(Br)
        header.append("WARC-Record-ID: <" + warcRecordID() + ">").append(Br)
        header.append("Content-Type: application/http; msgtype=response").append(Br)
        header.append("Content-Length: " + bytes.length).append(Br)
        header.append(Br)

        header.toString().getBytes(UTF8)
    }

    private def warcRecordID() = "urn:uuid:" + UUID.randomUUID()
}
