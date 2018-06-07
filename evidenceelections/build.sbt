name := """evidence-elections"""
version := "1.0-SNAPSHOT"
scalaVersion := "2.12.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
pipelineStages := Seq(digest)

libraryDependencies ++= Seq(
  jdbc,
  ehcache,
  ws,
  guice,
  "com.adrianhurt" %% "play-bootstrap" % "1.4-P26-B4-SNAPSHOT",
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
