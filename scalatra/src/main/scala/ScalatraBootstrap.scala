import com.mirae.shimpyo.RouteApp
import org.scalatra._
import org.slf4j.LoggerFactory
import slick.jdbc.JdbcBackend.Database

import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle{
  val logger = LoggerFactory.getLogger(getClass)

  override def init(context: ServletContext) {
    val db = System.getenv("RDS_HOSTNAME") match {
      case null => Database.forConfig("mydb")
      case _ => {
        Database.forURL(
          s"jdbc:mysql://shimpyoawsmysql.ca9ax6wszxzo.ap-northeast-2.rds.amazonaws.com:3306/shimpyo?serverTimezone=UTC&useSSL=false",
          "root",
          "12345678"
        )
      }
    }

    context.mount(new RouteApp(db), "/*")
//    context.mount(new ServletMain, "/*")
//    context.mount(new Articles, "/articles/*")
  }

  override def destroy(context:ServletContext) {
    super.destroy(context)
  }
}
