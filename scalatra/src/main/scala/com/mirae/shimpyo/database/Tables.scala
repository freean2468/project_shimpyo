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

    // Create
    // def drop() = db.run(DBIOAction.seq(accounts.schema.drop))
    def insert(account: Account) = db.run(accounts += account)

    def insert() = {
      val insertAction = DBIO.seq(
        accounts += Account("testId", "testPw"),
        accounts ++= Seq(
          Account("test1id", "test1pw"),
          Account("test2id", "test2pw"),
          Account("test3id", "test3pw")
        )
      )
      db.run(insertAction)
    }

    // Read
    def selectAll() = db.run(sql"select * from account_table".as[(String, String)])

    // Update
    def update(no: String, pwTo: String) = {
      val updateAction = (for { a <- accounts if a.no === no } yield a.pw).update(pwTo)
      db.run(updateAction)
    }


    // Delete
    def delete(): Unit = {
      val deleteAction = (accounts filter { _.no like "%test%" }).delete
      db.run(deleteAction)
    }

    // def find(no: String) = db.run((for (account <- accounts if account.no === no) yield account).result.headOption) // imperative way
    def find(no: String) = db.run(accounts.filter(_.no === no).result.headOption)


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

  get("/db/insert") {
    accounts.insert
  }

  get("/db/update/:pw") {
    val no = params("no")
    val pwTo = params("pwTo")
    accounts.update(no, pwTo)
  }

  get("/db/delete") {
    accounts.delete
  }

  get("/db/find") {
    accounts.find("1") map { s1 =>
      f"no : $s1, pw : " mkString "\n"
    }
  }

  get("/db/selectAll") {
    contentType = formats("json")
    accounts.selectAll()
  }

  get("/db/accounts") {
    accounts.accounts
  }
}

class SlickApp (val db: Database) extends ScalatraServlet with SlickRoutes {
  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global
}