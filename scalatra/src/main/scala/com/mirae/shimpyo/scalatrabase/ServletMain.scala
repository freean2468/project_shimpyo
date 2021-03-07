package com.mirae.shimpyo.scalatrabase

import org.scalatra._

class ServletMain extends ScalatraServlet {

  get("/") {
    //views.html.hello()

    contentType = "text/html"
    "Hi there!"
  }

  get("/hello/:name") {
    // Matches "GET /hello/foo" and "GET /hello/bar"
    // params("name") is "foo" or "bar"
    <p>Hello, {params("name")}</p>
  }

  get("/say/*/to/*") {
    // Matches "GET /say/hello/to/world"
    multiParams("splat") // == Seq("hello", "world")
  }

  get("/download/*.*") {
    // Matches "GET /download/path/to/file.xml"
    multiParams("splat") // == Seq("path/to/file", "xml")
  }

}
