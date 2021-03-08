package com.mirae.shimpyo

import com.mirae.shimpyo.database.Tables
import com.mirae.shimpyo.database.Tables.{Account, Repository}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{FutureSupport, ScalatraBase, ScalatraServlet}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext

trait Route extends ScalatraBase with JacksonJsonSupport with FutureSupport{
  // Sets up automatic case class to JSON output serialization, required by the JValueResult trait.
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  def db: Database
  val repository = new Repository(db)

  /*
    /db/ routing
   */
  get("/db/init") {
    db.run(Tables.accounts.result) map { xs =>
      xs map { case Account(s1, s2) => f"$s1, $s2" } mkString "\n"
    }
  }

  get("/db/insert") {
    repository.insert()
  }

  get("/db/update/:pw") {
    val no = params("no")
    val pwTo = params("pwTo")
    repository.update(no, pwTo)
  }

  get("/db/delete") {
    repository.delete()
  }

  get("/db/find") {
    repository.findAccount("1") map { s1 =>
      f"no : $s1, pw : " mkString "\n"
    }
  }

  get("/db/selectAll") {
    contentType = formats("json")
    repository.selectAll()
  }

  get("/db/accounts") {
    Tables.accounts
  }

  /*
    /service/ routing
   */
  get("/service/login/:id") {
    contentType = formats("json")
    repository.login(params("no"))
  }
}

class RouteApp (val db: Database) extends ScalatraServlet with Route {
  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}