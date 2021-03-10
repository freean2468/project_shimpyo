val ScalatraVersion = "2.7.1"

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / organization := "com.mirae"

lazy val hello = (project in file("."))
  .settings(
    name := "shimpyo",
    version := "0.0.1",
    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra" % ScalatraVersion,
      "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
      "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
      //"org.eclipse.jetty" % "jetty-webapp" % "9.4.35.v20201120" % "compile;provided;container",
      "org.eclipse.jetty" % "jetty-webapp" % "9.4.35.v20201120" % "provided;container",
      "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",

      // Database packages
      "mysql" % "mysql-connector-java" % "8.0.11",
      "com.typesafe.slick" %% "slick" % "3.3.2",

      // for Json formatting packages
      "org.scalatra" %% "scalatra-json" % "2.7.0",
      "org.json4s" %% "json4s-jackson" % "3.6.11",

      // for configuration
      "com.typesafe" % "config" % "1.4.1"
    ),
  )

enablePlugins(SbtTwirl)
enablePlugins(JettyPlugin)
enablePlugins(JavaAppPackaging)

//val ScalatraVersion = "2.7.1"
//
//ThisBuild / scalaVersion := "2.13.4"
//ThisBuild / organization := "com.mirae"
//
//lazy val app = project
//  .in(file("."))
//  .settings(
//    name := "shimpyo",
//    version := "0.0.1",
//    libraryDependencies ++= Seq(
//      "org.scalatra" %% "scalatra" % ScalatraVersion,
//      "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
//      "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
//      //"org.eclipse.jetty" % "jetty-webapp" % "9.4.35.v20201120" % "compile;provided;container",
//      "org.eclipse.jetty" % "jetty-webapp" % "9.4.35.v20201120" % "compile;provided;container",
//      "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
//
//      // Database packages
//      "mysql" % "mysql-connector-java" % "8.0.11",
//      "com.typesafe.slick" %% "slick" % "3.3.2",
//
//      // for Json formatting packages
//      "org.scalatra" %% "scalatra-json" % "2.7.0",
//      "org.json4s" %% "json4s-jackson" % "3.6.11",
//
//      // for configuration
//      "com.typesafe" % "config" % "1.4.1"
//    ),
//  )
//  .enablePlugins(SbtTwirl)
//  .enablePlugins(JettyPlugin)
//  .enablePlugins(JavaAppPackaging)
//
//lazy val stagePackage = project
//  .in(file("build/stage"))
//  .settings(
//    resourceDirectory in Compile := (resourceDirectory in (app, Compile)).value,
//    mappings in Universal += {
//      ((resourceDirectory in Compile).value / "stage.conf") -> "conf/application.conf"
//    },
//    libraryDependencies ++= Seq(
//      "org.eclipse.jetty" % "jetty-webapp" % "9.4.35.v20201120" % "provided;container"
//    )
//  )
//  .dependsOn(app)
//
//lazy val prodPackage = project
//  .in(file("build/develop"))
//  .settings(
//    resourceDirectory in Compile := (resourceDirectory in (app, Compile)).value,
//    mappings in Universal += {
//      ((resourceDirectory in Compile).value / "prod.conf") -> "conf/application.conf"
//    },
//    libraryDependencies ++= Seq(
//      "org.eclipse.jetty" % "jetty-webapp" % "9.4.35.v20201120" % "compile"
//    )
//  )
//  .dependsOn(app)
