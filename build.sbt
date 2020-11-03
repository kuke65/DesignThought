name := "designThought"

version := "0.1"

scalaVersion := "2.13.2"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint")


val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.1.10"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "org.slf4j" % "slf4j-api" % "1.7.30",
  "org.slf4j" % "slf4j-ext" % "1.7.30",
  "org.elasticsearch" % "elasticsearch" % "7.9.0",
  "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.9.0",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion,
  "org.apache.zookeeper" % "zookeeper" % "3.5.5"
    exclude("log4j", "log4j")
    exclude("org.slf4j", "slf4j-log4j12"),
)
