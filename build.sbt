import _root_.sbtassembly.AssemblyPlugin.autoImport._
import _root_.sbtassembly.PathList

lazy val commonSettings = Seq(
  name := "web2warc",
  version := "1.1",
  scalaVersion := "2.11.11",
  organization := "com.github.helgeho"
)

lazy val web2warc = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % "1.8.3",
      "commons-io" % "commons-io" % "2.4",
      "org.apache.httpcomponents" % "httpclient" % "4.5.1",
      "org.netpreserve.commons" % "webarchive-commons" % "1.1.5" excludeAll(
        ExclusionRule(organization = "org.apache.hadoop", name = "hadoop-core"))
    ),
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
      ),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    homepage := Some(url("https://github.com/helgeho/Web2Warc")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/helgeho/Web2Warc"),
        "scm:git@github.com:helgeho/Web2Warc.git"
      )
    ),
    developers := List(
      Developer(
        id    = "helgeho",
        name  = "Helge Holzmann",
        email = "holzmann@L3S.de",
        url   = url("http://www.HelgeHolzmann.de")
      )
    ),
    licenses := Seq("MIT" -> url("http://www.opensource.org/licenses/mit-license.php"))
  )

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false, cacheOutput = false)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}