package com.mirae.shimpyo.route

import com.mirae.shimpyo.database.{QuerySupport, Tables}
import com.mirae.shimpyo.database.Tables.Account
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{FutureSupport, ScalatraBase, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext

/**
 *  ScalatraBase는 Scalatra DSL을 구현해주고
 *  JacksonJsonSupport는 모든 데이터를 Json으로 암묵적 형변환을 구현해주고
 *  FutureSupport는 비동기 응답을 가능케 한다.
 *
 */
trait DBRoute extends ScalatraBase with JacksonJsonSupport with FutureSupport with QuerySupport {
  /** Sets up automatic case class to JSON output serialization, required by the JValueResult trait. */
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  def db: Database

  get("/init") {
    db.run(Tables.accounts.result) map { xs =>
      xs map { case Account(s1, s2) => f"$s1, $s2" } mkString "\n"
    }
  }

  get("/insert") {
    insert(db)
  }

  get("/update/:pw") {
    val no = params("no")
    val pwTo = params("pwTo")
    update(db, no, pwTo)
  }

  get("/delete") {
    delete(db)
  }

  get("/find") {
    findAccount(db, "1") map { s1 =>
      f"no : $s1, pw : " mkString "\n"
    }
  }

  get("/selectAll") {
    contentType = formats("json")
    selectAll(db)
  }

  get("/accounts") {
    Tables.accounts
  }

  get("/") {

  }

  post("/question") {

  }

  error {
    case e: NoSuchElementException => e.printStackTrace()
    case e: NumberFormatException => e.printStackTrace()
    case e: Exception => e.printStackTrace()
  }
}


/** DB query를 제공하는 routing 클래스
 *
 * @param db config 정보
 */
class DBRouteServlet (val db: Database) extends ScalatraServlet with DBRoute {
  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}
