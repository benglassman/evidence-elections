name := """evidence-elections"""
version := "1.0-SNAPSHOT"
scalaVersion := "2.12.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala,DockerPlugin)
pipelineStages := Seq(digest)

dockerRepository:= some("981392027332.dkr.ecr.us-east-1.amazonaws.com")

libraryDependencies ++= Seq(
  jdbc,
  ehcache,
  ws,
  guice,
  evolutions,
  "com.adrianhurt" %% "play-bootstrap" % "1.4-P26-B4-SNAPSHOT",
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  "mysql" % "mysql-connector-java" % "5.1.46",
  "org.scalikejdbc" %% "scalikejdbc" % "3.0.0",
  "org.scalikejdbc" %% "scalikejdbc-config"  % "3.0.0",
  "ch.qos.logback"  %  "logback-classic" % "1.2.3",
  "de.svenkubiak" % "jBCrypt" % "0.4.1"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
