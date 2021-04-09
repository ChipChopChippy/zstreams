ThisBuild / scalaVersion     := "2.13.5"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.srfsoftware"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "zstreams",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "1.0.5",
      "dev.zio" %% "zio-streams" % "1.0.5",
      "dev.zio" %% "zio-nio" % "1.0.0-RC10",
      "com.typesafe" % "config" % "1.4.1",
      "org.json4s" %% "json4s-jackson" % "3.7.0-M10",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.2",
      "org.apache.httpcomponents" % "httpclient" % "4.5.13",
      "org.apache.httpcomponents" % "httpcore" % "4.4.14",
      "dev.zio" %% "zio-test" % "1.0.5" % Test,
      "org.scalatest" %% "scalatest" % "3.2.7" % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
