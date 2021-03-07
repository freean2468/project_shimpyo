val ScalatraVersion = "2.7.1"

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / organization := "com.mirae"

lazy val hello = (project in file("."))
  .settings(
    name := "shimpyo",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra" % ScalatraVersion,
      "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
      "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
      "org.eclipse.jetty" % "jetty-webapp" % "9.4.35.v20201120" % "compile;container",
      "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",

      // Database packages
      "mysql" % "mysql-connector-java" % "8.0.11",
      "com.typesafe.slick" %% "slick" % "3.3.2",
      // no pooling with mysql
      //"com.mchange" % "c3p0" % "0.9.5.5",
      //"com.typesafe.slick" %% "slick-hikaricp" % "3.3.2"

      // for Json formatting packages
      "org.scalatra" %% "scalatra-json" % "2.7.0",
      "org.json4s" %% "json4s-jackson" % "3.6.11"

    ),
  )

enablePlugins(SbtTwirl)
enablePlugins(JettyPlugin)
enablePlugins(JavaAppPackaging)