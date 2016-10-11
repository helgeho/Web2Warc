import _root_.sbtassembly.AssemblyPlugin.autoImport._
import _root_.sbtassembly.PathList
import sbt.Keys._

lazy val commonSettings = Seq(
  name := "web2warc",
  version := "1.0",
  organization := "de.l3s"
)

lazy val web2warc = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % "1.8.3",
      "commons-io" % "commons-io" % "2.4",
      "org.apache.httpcomponents" % "httpclient" % "4.5.1",
      "org.netpreserve.commons" % "webarchive-commons" % "1.1.5" excludeAll(
        ExclusionRule(organization = "org.apache.hadoop"))
    ),
    resolvers ++= Seq(
      "cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos"
    )
  )

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false, cacheOutput = false)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
