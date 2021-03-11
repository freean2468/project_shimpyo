package com.mirae.shimpyo.database

import com.mirae.shimpyo.Util
import com.mirae.shimpyo.database.Tables.{Account, Diary, accounts, diaries}
import org.scalatra.BadRequest
import org.slf4j.LoggerFactory
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database

import java.util.Calendar
import scala.concurrent.Promise
import scala.util.{Failure, Success, Try}
import slick.jdbc.MySQLProfile.api._


/** Tables의 모방 테이블을 활용한 쿼리문 지원하는 trait, Route class에 mixing in!
 *
 * @param db 첫 servlet 생성 시 전달된 db config
 */
trait QuerySupport {
  import scala.concurrent.ExecutionContext.Implicits.global

  /** Create
   *
   */
  // def drop() = db.run(DBIOAction.seq(accounts.schema.drop))
  def insert(db: Database, account: Account) = db.run(accounts += account)
  def insert(db: Database, diary: Diary) = db.run(diaries += diary)

  def insert(db: Database) = {
    val insertAction = DBIO.seq(
      accounts += Account("testId", Some("testPw")),
      accounts ++= Seq(
        Account("test1id", Some("test1pw")),
        Account("test2id", Some("test2pw"))
      )
    )
    db.run(insertAction)
  }

  /** Read
   *
   */
  def selectAll(db: Database) =
    db.run(sql"select * from account_table".as[(String, String)])

  // def find(no: String) = db.run((for (account <- accounts if account.no === no) yield account).result.headOption) // imperative way
  def findAccount(db: Database, no: String) = db.run(accounts.filter(_.no === no).result.headOption)
  def findDiary(db: Database, no: String) = db.run(diaries.filter(_.no === no).result.headOption)
  def findDiaries(db: Database, no: String, firstDay: Int, lastDay: Int) =
    db.run(diaries.filter(d =>
      d.no === no && (d.dayOfYear >= firstDay && d.dayOfYear <= lastDay)).result)


  def login(db: Database, no:String) = {
    val logger = LoggerFactory.getLogger(getClass)
    val prom = Promise[Diary]()
    findAccount(db, no) onComplete {
      case Failure(e) => {
        prom.failure(e)
        e.printStackTrace()
      }
      case Success(count) => {
        count match {
          case None => {
            //              logger.info("count None")
            insert(db, Account(no, null))
            insert(db, Diary(no, Calendar.getInstance.get(Calendar.DAY_OF_YEAR), Some(""), null)) onComplete {
              case Failure(e) => {
                prom.failure(e)
                e.printStackTrace()
              }
              case Success(count) => {
                findDiary(db, no) onComplete {
                  case Success(r) => prom.complete(Try(r.get))
                  case Failure(e) => {
                    prom.failure(e)
                    e.printStackTrace()
                  }
                }
              }
            }
          }
          case _ => {
            //              logger.info("count Some, no : " + no)
            findDiary(db, no) onComplete {
              case Success(r) => {
                logger.info(r.get.toString)
                prom.complete(Try(r.get))
              }
              case Failure(e) => {
                prom.failure(e)
                e.printStackTrace()
              }
            }
          }
        }
      }
    }
    prom.future
  }

  def calendar(db: Database, no:String, month: Int) = {
    val days = Util.getDaysWithMonth(month)
    val logger = LoggerFactory.getLogger(getClass)
    //      logger.info("days.head : " + days.head + " days.last : " + days.last)
    val prom = Promise[Seq[Diary]]()
    findDiaries(db, no, days.head, days.last) onComplete {
      case Success(v) => prom.complete(Try(v))
      case Failure(e) => {
        prom.failure(e)
        e.printStackTrace()
      }
    }
    prom.future
  }

  /** Update
   *
   */
  def update(db: Database, no: String, pwTo: String) = {
    val updateAction = (for { a <- accounts if a.no === no } yield a.pw).update(pwTo)
    db.run(updateAction)
  }

  /** Delete
   *
   */
  def delete(db: Database): Unit = {
    val deleteAction = (accounts filter { _.no like "%test%" }).delete
    db.run(deleteAction)
  }
}
