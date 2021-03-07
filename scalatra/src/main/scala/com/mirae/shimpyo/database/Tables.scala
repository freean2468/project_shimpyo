package com.mirae.shimpyo.database

import com.mirae.shimpyo.database.Tables.{Account, AccountRepository}
import org.json4s.jackson.Serialization.formats
import org.scalatra.{FutureSupport, ScalatraBase, ScalatraServlet}
import slick.dbio.DBIOAction
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

object Tables {
  case class Account(no: String, pw: String)

  trait AccountTable {
    class Accounts(tag: Tag) extends Table[Account](tag, "account_table") {
      // Columns
      def no = column[String]("no", O.PrimaryKey)
      def pw = column[String]("pw")

      // Every table needs a * projection with the same type as the table's type parameter
      def * = (no, pw) <> (Account.tupled, Account.unapply)
    }
    val accounts = TableQuery[Accounts]
  }

  class AccountRepository(db: Database) extends AccountTable {
    import scala.concurrent.ExecutionContext.Implicits.global

    def presentAccountTable() = db.run(accounts.result)

    def init() = db.run(sql"select * from account_table".as[(String, String)])

    // def drop() = db.run(DBIOAction.seq(accounts.schema.drop))
    def insert(account: Account) = db.run(accounts += account)

    // def find(no: String) = db.run((for (account <- accounts if account.no === no) yield account).result.headOption) // imperative way
    def find(no: String) = db.run(accounts.filter(_.no === no).result.headOption)

    val setup = DBIO.seq(
      accounts += Account("test1", "123")
    )
    val setupFuture = db.run(setup)
  }
}

trait SlickRoutes extends ScalatraBase with JacksonJsonSupport with FutureSupport{
  // Sets up automatic case class to JSON output serialization, required by the JValueResult trait.
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  def db: Database
  val accounts = new AccountRepository(db)

  get("/db/init") {
    db.run(accounts.accounts.result) map { xs =>
      xs map { case Account(s1, s2) => f"$s1, $s2" } mkString "\n"
    }
  }

  get("/db/setup") {
    accounts.setupFuture
  }

  get("/db/find") {
    accounts.find("1") map { s1 =>
      f"no : $s1, pw : " mkString "\n"
    }
  }

  get("/db/selectAll") {
    contentType = formats("json")
    accounts.init
  }

  get("/db/accounts") {
    accounts.accounts
  }
}

class SlickApp (val db: Database) extends ScalatraServlet with SlickRoutes {
  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global
}