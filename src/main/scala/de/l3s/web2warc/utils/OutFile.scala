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

import java.io.{File, FileOutputStream, OutputStream, PrintStream}
import java.nio.file.{Path, Paths}
import java.util.zip.GZIPOutputStream

import scala.util.Try

class OutFile(val path: Path, append: Boolean, createPath: Boolean) {
  def this(path: String, append: Boolean = true, createPath: Boolean = true) = this(Paths.get(path), append, createPath)
  def this(path: Path) = this(path, true, true)
  def this(path: Path, append: Boolean) = this(path, append, true)

  def create(): Boolean = {
    val file = new File(path.toString)
    val dir = new File(file.getParent)
    dir.mkdirs()
    file.createNewFile()
  }

  private var _stream: FileOutputStream = null
  lazy val stream = {
    if (createPath) create()
    _stream = new FileOutputStream(path.toString, append)
    _stream
  }

  private var _gzip: GZIPOutputStream = null
  lazy val gzip = {
    _gzip = new GZIPOutputStream(stream)
    _gzip
  }

  private var _outStream: OutputStream = null
  def outStream = _outStream
  def outStream_=(stream: OutputStream) = {
    if (_out != null) throw new RuntimeException("The 'out' stream must be set before calling and output methods.")
    _outStream = stream
  }

  private var _out: OutputStream = null
  lazy val out = {
    if (_outStream == null) _outStream = stream
    _out = _outStream
    _out
  }

  private var _print: PrintStream = null
  lazy val print = {
    _print = new PrintStream(out)
    _print
  }

  def flush() = {
    if (_print != null) Try { _print.flush() }
    if (_out != null) Try { _print.flush() }
    if (_gzip != null) Try { _gzip.flush() }
    if (_stream != null) Try { _stream.flush() }
  }

  def close() = {
    flush()
    if (_print != null) Try { _print.close() }
    if (_out != null) Try { _print.close() }
    if (_gzip != null) Try {
      _gzip.finish()
      _gzip.close()
    }
    if (_stream != null) Try { _stream.close() }
  }
}
