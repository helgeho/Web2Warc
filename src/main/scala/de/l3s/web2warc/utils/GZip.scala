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

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import org.apache.commons.io.IOUtils

object GZip {
  def compress(bytes: Array[Byte]): Array[Byte] = {
    val in = new ByteArrayInputStream(bytes)
    val out = new ByteArrayOutputStream()
    val compressed = new GZIPOutputStream(out)
    IOUtils.copy(in, compressed)
    in.close()
    compressed.flush()
    compressed.finish()
    compressed.close()
    out.flush()
    out.close()
    out.toByteArray
  }

  def uncompress(bytes: Array[Byte]): Array[Byte] = {
    val in = new ByteArrayInputStream(bytes)
    val compressed = new GZIPInputStream(in)
    val out = new ByteArrayOutputStream()
    IOUtils.copy(compressed, out)
    compressed.close()
    in.close()
    out.flush()
    out.close()
    out.toByteArray
  }
}
