import com.mirae.shimpyo.database.SlickApp
import org.scalatra._
import org.slf4j.LoggerFactory
import slick.jdbc.JdbcBackend.Database

import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle{
  val logger = LoggerFactory.getLogger(getClass)

  override def init(context: ServletContext) {
    val db = Database.forConfig("mydb")

    context.mount(new SlickApp(db), "/*")
//    context.mount(new ServletMain, "/*")
//    context.mount(new Articles, "/articles/*")
  }

  override def destroy(context:ServletContext) {
    super.destroy(context)
  }
}
