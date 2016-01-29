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

import java.io.{InputStream, ByteArrayOutputStream, PrintWriter}
import java.net.URI

import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.ssl.SSLContexts

import scala.collection.immutable.ListMap
import scala.util.Try

object HttpClientReader {
  def defaultHeaders = ListMap[String, String](
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
  )

  lazy val client = HttpClientBuilder.create().setSSLContext(SSLContexts.createSystemDefault()).build()

  def get(url: String, headers: Map[String, String] = defaultHeaders): Array[Byte] = {
    val uri = new URI(url)

    val request = new HttpGet(uri)
    for ((k,v) <- headers) request.addHeader(k, v)

    var response: CloseableHttpResponse = null
    var entityStream: InputStream = null
    var responseBytes = new ByteArrayOutputStream()
    var responsePrint = new PrintWriter(responseBytes)
    try {
      response = client.execute(request)

      // headers
      responsePrint.println(response.getStatusLine.toString)
      for (header <- response.getAllHeaders) responsePrint.println(s"${header.getName}: ${header.getValue}")

      // empty line
      responsePrint.println()
      responsePrint.flush()

      // contents
      entityStream = response.getEntity.getContent
      IOUtils.copy(entityStream, responseBytes)
      responseBytes.flush()

      // two empty lines
      responsePrint.println()
      responsePrint.println()
      responsePrint.flush()

      // done
      responseBytes.flush()
      responseBytes.toByteArray
    } finally {
      if (response != null) Try {response.close()}
      if (entityStream != null) Try {entityStream.close()}
    }
  }
}
