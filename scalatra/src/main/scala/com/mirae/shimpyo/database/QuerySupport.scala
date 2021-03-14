package com.mirae.shimpyo.database

import com.mirae.shimpyo.Util
import com.mirae.shimpyo.database.Tables.{Account, Diary, accounts, diaries}
import org.json4s.jackson.JsonMethods.{compact, render}
import org.json4s.JsonDSL._

import org.scalatra.{ActionResult, BadRequest, Ok, halt}
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
    val prom = Promise[Int]()
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
              prom.complete(Try(res2))
            }
            case Failure(e) => {
              prom.failure(e)
              e.printStackTrace()
            }
          }
          case None => insert(db, Diary(no, dayOfYear, Option(answer), Option(photo))) onComplete {
            case Success(res2) => {
              logger.info(s"answer in insert success. res2 : ${res2}")
              prom.complete(Try(res2))
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
    val prom = Promise[ActionResult]()
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
              case Success(count) => prom.complete(Try(Ok(Diary(no, dayOfYear, null, null))))
            }
          }
          case Some(x) => {
            findDiary(db, no, dayOfYear) onComplete {
              case Success(r) => {
                r match {
                  case Some(diary) => {
                    val logger = LoggerFactory.getLogger(getClass)
//                    logger.info("byte Array photo length : " + diary.photo.get.length)
                    val sPhoto = new String(diary.photo.get, StandardCharsets.UTF_8)
//                    logger.info("sPhoto length : " + sPhoto.length)
                    prom.complete(Try(Ok(("no" -> diary.no) ~ ("dayOfYear" -> diary.dayOfYear) ~ ("answer" -> diary.answer.get) ~ ("photo" -> sPhoto))))
                  }
                  case None => prom.complete(Try(Ok(Diary(no, dayOfYear, null, null))))
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
