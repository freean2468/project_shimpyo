package com.mirae.shimpyo.scalatrabase

import org.scalatra.ScalatraServlet

class Articles extends ScalatraServlet{

  get("/:id") {  //  <= this is a route matcher
    // this is an action
    // this action would show the article which has the specified :id
  }

  post("/articles") {
    // submit/create an article
  }

  put("/articles/:id") {
    // update the article which has the specified :id
  }

  delete("/articles/:id") {
    // delete the article with the specified :id
  }
}
