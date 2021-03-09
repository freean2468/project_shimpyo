package com.mirae.shimpyo

import com.mirae.shimpyo.database.Tables
import com.mirae.shimpyo.database.Tables.{Account, Repository}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{AsyncResult, FutureSupport, Ok, ScalatraBase, ScalatraServlet}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

/**
 *  ScalatraBase는 Scalatra DSL을 구현해주고
 *  JacksonJsonSupport는 모든 데이터를 Json으로 암묵적 형변환을 구현해주고
 *  FutureSupport는 비동기 응답을 가능케 한다.
 *
 */
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
    new AsyncResult { override val is =
      Future {
        contentType = formats("json")
        repository.login(params("no"))
      }
    }
  }

  get("/service/calendar/:id") {
    new AsyncResult { override val is =
      Future {
        contentType = formats("json")
        repository.calendar(params("no"), params("m").toInt)
      }
    }
  }

  error {
    case e: NoSuchElementException => e.printStackTrace()
    case e: NumberFormatException => e.printStackTrace()
    case e: Exception => e.printStackTrace()
  }
}

class RouteApp (val db: Database) extends ScalatraServlet with Route {
  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
//  protected implicit def executor: ExecutionContext = system.dispatcher
}
