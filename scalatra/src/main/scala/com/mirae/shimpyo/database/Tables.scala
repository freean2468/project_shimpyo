package com.mirae.shimpyo.database

import com.mirae.shimpyo.Util
import org.scalatra.{AsyncResult, Ok}
import org.slf4j.LoggerFactory
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._

import java.util.Calendar
import scala.concurrent.Promise
import scala.util.{Failure, Success, Try}

/** Slick에서 제공하는 Functional Relational Mapping(FRM)을 담은 object
 * 이 안에서 서비스에 필요한 테이블 쿼리를 정의한다.
 *
 */
object Tables {

  /** account_table의 한 레코드를 모방한 case class 자동 형변환에 사용
   *
   * @param no 유저의 카카오톡 회원번호
   * @param pw (Optionable) 유저의 화면 잠금 비밀번호
   */
  case class Account(no: String, pw: Option[String])

  /** question_table 의 한 레코드를 모방한 case class 자동 형변환에 사용
   *
   * @param dayOfYear 365일 중 몇 번째 일인지
   * @param question 해당 일의 질문
   */
  case class Question(dayOfYear: Int, question: String)

  /** diary_table 의 한 레코드를 모방한 case class 자동 형변환에 사용
   *
   * @param no 유저의 카카오톡 회원번호(fk)
   * @param dayOfYear 365일 중 몇 번째 일인지(fk)
   * @param answer (Optionable) 유저가 작성한 대답
   * @param photo (Optionable) 그날 저장한 사진
   */
  case class Diary(no: String, dayOfYear: Int, answer: Option[String], photo: Option[Array[Byte]])

  /** db의 account_table을 모방한 클래스
   *
   * @param tag 테이블 이름
   */
  class Accounts(tag: Tag) extends Table[Account](tag, "account_table") {
    /** Columns */
    def no = column[String]("no", O.PrimaryKey)
    def pw = column[String]("pw")

    /** Every table needs a * projection with the same type as the table's type parameter */
    def * = (no, pw.?) <> (Account.tupled, Account.unapply)
  }

  /** account_table과의 쿼리를 담당할 변수
   *
   */
  val accounts = TableQuery[Accounts]

  /** db의 question_table을 모방한 클래스
   *
   * @param tag 테이블 이름
   */
  class Questions(tag: Tag) extends Table[Question](tag, "question_table") {
    /** Columns */
    def dayOfYear = column[Int]("dayOfYear", O.PrimaryKey)
    def question = column[String]("question")

    /** Every table needs a * projection with the same type as the table's type parameter */
    def * =
      (dayOfYear, question) <> (Question.tupled, Question.unapply)
  }

  /** question_table과의 쿼리를 담당할 변수
   *
   */
  val questions = TableQuery[Questions]

  /** db의 diary_table을 모방한 클래스
   *
   * @param tag 테이블 이름
   */
  class Diaries(tag: Tag) extends Table[Diary](tag, "diary_table") {
    /** Columns */
    def no = column[String]("no")
    def dayOfYear = column[Int]("dayOfYear")
    def answer = column[String]("answer")
    def photo = column[Array[Byte]]("photo")

    /** foreign key */
    def account =
      foreignKey("fk_no", no, accounts)(_.no,
        onUpdate = ForeignKeyAction.Cascade,
        onDelete=ForeignKeyAction.Cascade)

    /** foreign key */
    def question =
      foreignKey("fk_dayOfYear", dayOfYear, questions)(_.dayOfYear,
        onUpdate = ForeignKeyAction.Cascade,
        onDelete=ForeignKeyAction.Restrict)

    def * =
      (no, dayOfYear, answer.?, photo.?) <> (Diary.tupled, Diary.unapply)
  }

  /** db의 diary_table과의 쿼리를 담당할 변수
   *
   */
  val diaries = TableQuery[Diaries]

  /** 위의 모방 테이블을 활용한 쿼리문을 모아놓은 클래스
   *
   * @param db 첫 servlet 생성 시 전달된 db config
   */
  class Repository(db: Database) {
    import scala.concurrent.ExecutionContext.Implicits.global

    /** Create */
    // def drop() = db.run(DBIOAction.seq(accounts.schema.drop))
    def insert(account: Account) = db.run(accounts += account)
    def insert(diary: Diary) = db.run(diaries += diary)

    def insert() = {
      val insertAction = DBIO.seq(
        accounts += Account("testId", Some("testPw")),
        accounts ++= Seq(
          Account("test1id", Some("test1pw")),
          Account("test2id", Some("test2pw"))
        )
      )
      db.run(insertAction)
    }

    /** Create End */

    /** Read */
    def selectAll() =
      db.run(sql"select * from account_table".as[(String, String)])

    // def find(no: String) = db.run((for (account <- accounts if account.no === no) yield account).result.headOption) // imperative way
    def findAccount(no: String) = db.run(accounts.filter(_.no === no).result.headOption)
    def findDiary(no: String) = db.run(diaries.filter(_.no === no).result.headOption)
    def findDiaries(no: String, firstDay: Int, lastDay: Int) =
      db.run(diaries.filter(d =>
        d.no === no && (d.dayOfYear >= firstDay && d.dayOfYear <= lastDay)).result)


    def login(no:String) = {
      val logger = LoggerFactory.getLogger(getClass)
      val prom = Promise[Diary]()
      findAccount(no) onComplete {
        case Success(count) => {
          count match {
            case None => {
//              logger.info("count None")
              insert(Account(no, null))
              insert(Diary(no, Calendar.getInstance.get(Calendar.DAY_OF_YEAR), Some(""), null)) onComplete {
                case Success(count) => {
                  findDiary(no) onComplete {
                    case Success(r) => prom.complete(Try(r.get))
                    case Failure(e) => e.printStackTrace()
                  }
                }
                case Failure(e) => e.printStackTrace()
              }
            }
            case _ => {
//              logger.info("count Some, no : " + no)
              findDiary(no) onComplete {
                case Success(r) => {
                  logger.info(r.get.toString)
                  prom.complete(Try(r.get))
                }
                case Failure(e) => e.printStackTrace()
              }
            }
          }
        }
        case Failure(e) => e.printStackTrace()
      }
      prom.future
    }

    def calendar(no:String, month: Int) = {
      val days = Util.getDaysWithMonth(month)
      val logger = LoggerFactory.getLogger(getClass)
//      logger.info("days.head : " + days.head + " days.last : " + days.last)
      val prom = Promise[Seq[Diary]]()
      findDiaries(no, days.head, days.last) onComplete {
        case Success(v) => prom.complete(Try(v))
        case Failure(e) => e.printStackTrace()
      }
      prom.future
    }

    /** Read End */

    /** Update */
    def update(no: String, pwTo: String) = {
      val updateAction = (for { a <- accounts if a.no === no } yield a.pw).update(pwTo)
      db.run(updateAction)
    }

    /** Update End */

    /** Delete */
    def delete(): Unit = {
      val deleteAction = (accounts filter { _.no like "%test%" }).delete
      db.run(deleteAction)
    }

    /** Delete End */
  }
}