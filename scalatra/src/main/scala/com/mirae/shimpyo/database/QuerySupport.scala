package com.mirae.shimpyo.database

import com.mirae.shimpyo.database.Tables.{Account, Diary, accounts, diaries}
import com.mirae.shimpyo.helper.Util
import org.json4s.jackson.JsonMethods.{compact, render}
import org.json4s.JsonDSL._
import org.scalatra.{ActionResult, BadRequest, NotFound, Ok, halt}
import org.slf4j.LoggerFactory
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Promise
import scala.util.{Failure, Success, Try}
import slick.jdbc.MySQLProfile.api._

import java.nio.charset.StandardCharsets


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

  def answer(db: Database, no: String, dayOfYear: Int, answer: String, photo: Array[Byte]) = {
    val prom = Promise[ActionResult]()
    val logger = LoggerFactory.getLogger(getClass)
    logger.info("answer in QuerySupport")
    findDiary(db, no, dayOfYear) onComplete {
      case Failure(e) => {

        logger.info("answer in QuerySupport failure")
        prom.failure(e)
        e.printStackTrace()
      }
      case Success(res) => {
        res match {
          case Some(_) => updateDiary(db, Diary(no, dayOfYear, Option(answer), Option(photo))) onComplete {
            case Success(res2) => {
              logger.info(s"answer in update success. res2 : ${res2}")
              prom.complete(Try(Ok(res2)))
            }
            case Failure(e) => {
              prom.failure(e)
              e.printStackTrace()
            }
          }
          case None => insert(db, Diary(no, dayOfYear, Option(answer), Option(photo))) onComplete {
            case Success(res2) => {
              logger.info(s"answer in insert success. res2 : ${res2}")
              prom.complete(Try(Ok(res2)))
            }
            case Failure(e) => {
              prom.failure(e)
              e.printStackTrace()
            }
          }
        }
      }
    }
    prom.future
  }

  /** Read
   *
   */
  def selectAll(db: Database) =
    db.run(sql"select * from account_table".as[(String, String)])

  // def find(no: String) = db.run((for (account <- accounts if account.no === no) yield account).result.headOption) // imperative way
  def findAccount(db: Database, no: String) =
    db.run(accounts.filter(_.no === no).result.headOption)

  def findDiary(db: Database, no: String, dayOfYears: Int) =
    db.run(diaries.filter(_.no === no).filter(_.dayOfYear === dayOfYears).result.headOption)

  def findDiaries(db: Database, no: String, firstDay: Int, lastDay: Int) =
    db.run(diaries.filter(d =>
      d.no === no && (d.dayOfYear >= firstDay && d.dayOfYear <= lastDay)).result)

  /**
   * 유저가 로그인 시 호출하는 함수. 회원 번호를 받아 account_table에 계정이 없으면 새로 생성한다.
   * 기존 유저이면서 dayOfYear에 작성한 diary record가 있다면 해당 값들을 반환해주고 없다면 null로 셋팅해 반환.
   * @param db
   * @param no 유저 카카오톡 회원 번호
   * @return 비동기 diary record
   */
  def login(db: Database, no:String, dayOfYear:Int) = {
    val logger = LoggerFactory.getLogger(getClass)
    val prom = Promise[ActionResult]()

    logger.info(s"no : ${no}, dayOfYear : ${dayOfYear}")

    findAccount(db, no) onComplete {
      case Failure(e) => {
        prom.failure(e)
        e.printStackTrace()
      }
      case Success(count) => {
        count match {
          case None => {
            insert(db, Account(no, null)) onComplete {
              case Failure(e) => {
                prom.failure(e)
                e.printStackTrace()
              }
              case Success(count) => prom.complete(Try(Ok(Diary(no, dayOfYear, Option(""), null))))
            }
          }
          case Some(x) => {
            findDiary(db, no, dayOfYear) onComplete {
              case Success(r) => {
                r match {
                  case Some(diary) => {
//                    logger.info(s"no : ${diary.no}, dayOfYear : ${diary.dayOfYear}, answer : ${diary.answer}, photo : ${diary.photo}")
                    val sPhoto = Util.convertBytesArrayToString(diary.photo.get)
                    prom.complete(Try(Ok(("no" -> diary.no) ~ ("dayOfYear" -> diary.dayOfYear) ~
                      ("answer" -> diary.answer.get) ~ ("photo" -> sPhoto))))
                  }
                  case None => prom.complete(Try(Ok(Diary(no, dayOfYear, Option(""), null))))
                }
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

  /** RequestDiary 요청에서 호출되는 함수
   * diary를 찾아 특정한 status코드를 입혀 반환해준다.
   *
   * @param db
   * @param no : 회원번호
   * @param dayOfYear : diary가 저장된 특정일
   */
  def retrieveEachDiary(db: Database, no:String, dayOfYear : Int) = {
    val logger = LoggerFactory.getLogger(getClass)
    val prom = Promise[ActionResult]()
    findDiary(db, no, dayOfYear) onComplete {
      case Failure(e) => {
        prom.failure(e)
        e.printStackTrace()
      }
      case Success(s) => s match {
        case None => {
//          logger.info(s"no : ${no}, dayOfYear : ${dayOfYear} nothing found!")
          prom.complete(Try(NotFound(null)))
        }
        case Some(diary) => {
//          logger.info(s"no : ${diary.no}, dayOfYear : ${diary.dayOfYear}, answer : ${diary.answer}, photo : ${diary.photo}")
          val sPhoto = Util.convertBytesArrayToString(diary.photo.get)
          prom.complete(Try(Ok(("no" -> diary.no) ~ ("dayOfYear" -> diary.dayOfYear) ~
            ("answer" -> diary.answer.get) ~ ("photo" -> sPhoto))))
        }
      }
    }
    prom.future
  }

  /** Update
   *
   */
  def updateAccountPassword(db: Database, newA:Account) = {
    val updateAction = (for {a <- accounts if a.no === newA.no} yield a.pw).update(newA.pw.orNull)
    db.run(updateAction)
  }

  def updateDiary(db: Database, newD: Diary) = {
    val updateAction = (for {
      d <- diaries if d.no === newD.no && d.dayOfYear === newD.dayOfYear
    } yield (d.answer, d.photo)).update((newD.answer.orNull, newD.photo.get))
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
