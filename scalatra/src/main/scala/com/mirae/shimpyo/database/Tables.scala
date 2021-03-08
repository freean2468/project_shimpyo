package com.mirae.shimpyo.database

import org.slf4j.LoggerFactory
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._

import java.util.Calendar

object Tables {
  case class Account(no: String, pw: String)
  case class Question(dayOfYear: Int, question: String)
  case class Diary(no: String, dayOfYear: Int, answer: String, photo: Array[Byte])

  class Accounts(tag: Tag) extends Table[Account](tag, "account_table") {
    // Columns
    def no = column[String]("no", O.PrimaryKey)
    def pw = column[String]("pw")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (no, pw) <> (Account.tupled, Account.unapply)
  }
  val accounts = TableQuery[Accounts]

  class Questions(tag: Tag) extends Table[Question](tag, "question_table") {
    // Columns
    def dayOfYear = column[Int]("dayOfYear", O.PrimaryKey)
    def question = column[String]("question")

    // Every table needs a * projection with the same type as the table's type parameter
    def * =
      (dayOfYear, question) <> (Question.tupled, Question.unapply)
  }
  val questions = TableQuery[Questions]

  class Diaries(tag: Tag) extends Table[Diary](tag, "diary_table") {
    def no = column[String]("no")
    def dayOfYear = column[Int]("dayOfYear")
    def answer = column[String]("answer")
    def photo = column[Array[Byte]]("photo")

    def account =
      foreignKey("fk_no", no, accounts)(_.no,
        onUpdate = ForeignKeyAction.Cascade,
        onDelete=ForeignKeyAction.Cascade)

    def question =
      foreignKey("fk_dayOfYear", dayOfYear, questions)(_.dayOfYear,
        onUpdate = ForeignKeyAction.Cascade,
        onDelete=ForeignKeyAction.Restrict)

    def * =
      (no, dayOfYear, answer, photo) <> (Diary.tupled, Diary.unapply)
  }
  val diaries = TableQuery[Diaries]

  class Repository(db: Database) {
    import scala.concurrent.ExecutionContext.Implicits.global

    // Create
    // def drop() = db.run(DBIOAction.seq(accounts.schema.drop))
    def insert(account: Account) = db.run(accounts += account)
    def insert(diary: Diary) = db.run(diaries += diary)

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
    def selectAll() =
      db.run(sql"select * from account_table".as[(String, String)])

    def login(no:String) = {
      val count = findAccount(no)
      val logger = LoggerFactory.getLogger(getClass)

      logger.debug("count : " + count)


      if (count == 0) {
        insert(Account(no, null))
        insert(Diary(no, Calendar.getInstance.get(Calendar.DAY_OF_YEAR), "", null))
      }

      findDiary(no)
    }

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
    def findAccount(no: String) = db.run(accounts.filter(_.no === no).result.headOption)
    def findDiary(no: String) = db.run(diaries.filter(_.no === no).result.headOption)
  }
}