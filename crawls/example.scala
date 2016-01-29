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