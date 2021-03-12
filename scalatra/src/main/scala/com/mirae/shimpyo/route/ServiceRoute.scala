package com.mirae.shimpyo.route

import com.mirae.shimpyo.database.{QuerySupport}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{AsyncResult, FutureSupport, ScalatraBase, ScalatraServlet}
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ExecutionContext, Future}

/**
 *  ScalatraBase는 Scalatra DSL을 구현해주고
 *  JacksonJsonSupport는 모든 데이터를 Json으로 암묵적 형변환을 구현해주고
 *  FutureSupport는 비동기 응답을 가능케 한다.
 *
 */
trait ServiceRoute extends ScalatraBase with JacksonJsonSupport with FutureSupport with QuerySupport{
  /** Sets up automatic case class to JSON output serialization, required by the JValueResult trait. */
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  def db: Database

  /**
   *  get
   */
  get("/login/:id") {
    new AsyncResult { override val is =
      Future {
        contentType = formats("json")

        /**
         * dayOfYear가 서버와 다르면 차단하는 로직이 필요할까?
         * 고민해보자.
         */
        login(db, params("no"), params("d").toInt)
      }
    }
  }

  get("/calendar/:id") {
    new AsyncResult { override val is =
      Future {
        contentType = formats("json")
        calendar(db, params("no"), params("m").toInt)
      }
    }
  }

  /**
   *  post
   */
  post("/answer/:id") {
    new AsyncResult { override val is =
      Future {
        contentType = formats("json")
        answer(db, params("no"), params("d").toInt, params("a"), params("p").getBytes)
      }
    }
  }

  /** 굳이 abusing 들에게 친절한 안내메세지를 보내지와 응답을 보내줄 필요는 없지 않을까?
   *  내 쪽에서만 어떤 에러가 있었는지 추적가능하도록 만들자.
   *
   */
  error {
    case e: NoSuchElementException => e.printStackTrace()
    case e: NumberFormatException => e.printStackTrace()
    case e: Exception => e.printStackTrace()
  }
}

/** 서비스를 제공하는 routing 클래스
 *
 * @param db config 정보
 */
class ServiceRouteServlet (val db: Database) extends ScalatraServlet with ServiceRoute {
  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}