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

package de.l3s.web2warc.utils

import java.io.{InputStream, OutputStream, PrintWriter}
import java.net.{Socket, URI}

import org.apache.commons.io.IOUtils
import org.apache.http.ProtocolVersion
import org.apache.http.client.methods.HttpGet

import scala.collection.immutable.ListMap
import scala.util.Try

object SimpleHttpReader {
  val DefaultPort = 80

  def defaultHeaders = ListMap[String, String](
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
  )

  def get(url: String, headers: Map[String, String] = defaultHeaders): Array[Byte] = {
    val uri = new URI(url)
    val get = new HttpGet(uri)
    get.setProtocolVersion(new ProtocolVersion("HTTP", 1, 0))

    val uriPort = uri.getPort
    val requestPort = if (uriPort < 0) DefaultPort else uriPort

    var in: InputStream = null
    var request: PrintWriter = null
    var out: OutputStream = null
    var socket: Socket = null
    try {
      socket = new Socket(uri.getHost, requestPort)
      out = socket.getOutputStream

      request = new PrintWriter(out)
      request.println(get.getRequestLine)
      request.println("Host: " + uri.getHost)
      for ((k,v) <- headers) request.println(s"$k: $v")
      request.println("")
      request.flush()

      in = socket.getInputStream
      IOUtils.toByteArray(in)
    } finally {
      if (request != null) Try {request.close()}
      if (out != null) Try {out.close()}
      if (in != null) Try{in.close()}
      if (socket != null) Try{socket.close()}
    }
  }
}
