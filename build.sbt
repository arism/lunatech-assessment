name := "lunatech-assessment"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.8",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.8",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.8",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

enablePlugins(JavaAppPackaging)
