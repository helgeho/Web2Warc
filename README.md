### Web2Warc - your custom Web crawler

An easy-to-use and highly customizable crawler that enables you to create your own little Web archives (WARC/CDX).

#### Setup

(if you want to use *Web2Warc* as a library in your own project, please read [here](#maven-central))

To use Web2Warc you need to have [Scala](http://www.scala-lang.org) as well as [SBT](http://www.scala-sbt.org). Both are free and quickly installed.

As the next step clone and build this repository:

1. `git clone https://github.com/helgeho/Web2Warc.git`
2. `sbt assembly` (inside the project folder)

Now you should have a `web2warc-assembly-1.0.jar` file under `target/scala-2.10`. If that's the case, then you are all set and ready to run your first crawl.

#### Usage

We provide an example crawl specification under [`crawls/example.scala`](crawls/example.scala). In order to create your own one, simply copy this file and modify it according to your requirements.

To run a crawl specification (here `example.scala`) just run `scala -cp ../target/scala-2.10/web2warc-assembly-1.0.jar example.scala` inside the `crawls` folder, or use the provided `run.sh` script:

`./run.sh example.scala`

The resulting CDX and WARC files can be found under `crawls/out` or the path specified in the spec.

The example crawl specification with comments looks shown below. Any property that's not relevant for you can simply be omitted and default values will be applied.
(Developers, please have a look at `Web2Warc.strategy` and `Web2Warc.strategyDef` to implement your fully customized crawling behavior)

```scala
import de.l3s.web2warc.Web2Warc

Web2Warc.crawl.name = "Example" // the name of the crawl
Web2Warc.crawl.publisher = "Anonymous" // your name or company, just for the records, not sent to the server

Web2Warc.writer.path = "out" // the output path

Web2Warc.spec.maxLevel = 10 // follow at most 10 links
Web2Warc.spec.urlRegex = "[^:]+://([^/]*.|)example.com(/.*|$)" // crawl only within the domain example.com
Web2Warc.spec.preserveUrlRegex = ".*" // preserve all crawled URLs (default)
Web2Warc.spec.followRedirects = true // follow redirects (default)
Web2Warc.spec.increaseLevelOnRedirect = true // following redirects increases the level, just like following a link (default)

Web2Warc.seeds += "http://www.example.com" // where to start crawling (multiple seeds possible, just add more of these lines)

Web2Warc.run()
```

#### Analysis / Corpus Extraction

In order to analyze crawls created with Web2Warc or to build a corpus for your research from the resulting archive, please have a look at the [ArchiveSpark](https://github.com/helgeho/ArchiveSpark) project.

#### Maven Central

If you want to use *Web2Warc* as a library in your own project, you can get it from Maven Central by adding it as a dependency to your `build.sbt`:
```
libraryDependencies += "com.github.helgeho" % "web2warc" % "1.1"
```

#### License

The MIT License (MIT)

Copyright (c) 2016 Helge Holzmann

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
