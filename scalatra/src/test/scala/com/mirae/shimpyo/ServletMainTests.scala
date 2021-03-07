package com.mirae.shimpyo

import com.mirae.shimpyo.scalatrabase.ServletMain
import org.scalatra.test.scalatest._

class ServletMainTests extends ScalatraFunSuite {

  addServlet(classOf[ServletMain], "/*")

  test("GET / on ServletMain should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
