import sbt.Keys.{scalaVersion, version}

val circeVersion = "0.13.0"
//sbt 'set crossScalaVersions := Seq("2.11.8", "2.12.6")' '+compile'

lazy val `course-work` = project
  .in(file("."))
  .settings(
    name in ThisBuild := "CourceWork",
    version in ThisBuild := "0.1",
    scalaVersion in ThisBuild := "2.13.4",


    libraryDependencies ++= Seq(
      // "com.bot4s" %% "telegram-akka" % "4.4.0-RC2",
      "org.typelevel" %% "cats-core" % "2.1.1",
      "org.typelevel" %% "cats-effect" % "2.1.4",

      "org.augustjune" %% "canoe" % "0.5.1",
      "com.typesafe.slick" %% "slick" % "3.3.3",
      "org.scalatest" %% "scalatest" % "3.2.0" % Test,
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",

      "org.xerial" % "sqlite-jdbc" % "3.7.2",

      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      "com.typesafe.akka" %% "akka-actor" % "2.6.10",
      "com.typesafe.akka" %% "akka-stream" % "2.6.10",
      "com.typesafe.akka" %% "akka-http" % "10.2.1",
      "de.heikoseeberger" %% "akka-http-circe" % "1.35.2",

      "com.beachape" %% "enumeratum" % "1.6.1",
      "com.beachape" %% "enumeratum-circe" % "1.6.1",

      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "ch.qos.logback" % "logback-classic" % "1.2.3",

      "org.scalatest" %% "scalatest" % "3.2.0" % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.10" % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % "10.2.1" % Test,
      "org.scalamock" %% "scalamock" % "4.4.0" % Test

    )

  )

//name := "CourceWork"
//
//version := "0.1"
//
//scalaVersion := "2.13.3"
