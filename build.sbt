name := "trojan-gate"

version := "1.0.0"

scalaVersion := "2.11.6"

val commonSettings = Seq(
  organization := "com.ithaca.coffee",
  version := "1.0.0",

  scalaVersion := "2.11.6",

  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),

  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.3.11",
    "com.typesafe.akka" %% "akka-http-experimental" % "1.0-RC3"
  )
)

lazy val app = (project in file("app")) settings commonSettings
lazy val root = (project in file(".")) aggregate app
