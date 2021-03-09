import com.mirae.shimpyo.{RouteApp}
import org.scalatra._
import slick.jdbc.JdbcBackend.Database

import javax.servlet.ServletContext

/** Scala 언어 기반 Web Framework Scalatra를 이용한 diary 서비스를 제공하는 shimpyo의 웹서버
 *
 * @author 송훈일(sensebe)
 * @since 2021.03.09 ~
 * @version 0.1
 */

/** Scala 언어 기반 Web Framework Scalatra의 main 함수격인 클래스.
 *
 */
class ScalatraBootstrap extends LifeCycle{

  /** Scalatra 앱 실행 시 실행되는 초기화 함수. servlet에 routing class를 mount
   *
   * @param context 현재 servlet context
   */
  override def init(context: ServletContext) {
    /** System.getenv를 통해 현재 AWS RDS에서 가동 중인 MySQL Instance Hostname을 가져와 현재 환경이 local인지 ec2인지 구분한다.
     *  db 정보는 application.conf 파일에 따로 보관하며 이는 github을 통해 노출하지 않는다.
     */
    val db = System.getenv("RDS_HOSTNAME") match {
      case null => Database.forConfig("localMySQL")
      case _ => Database.forConfig("RDSMySQL")
    }
    context.mount(new RouteApp(db), "/*")
  }

  /** servlet 자원 종료
   *
   * @param context 현재 servlet context
   */
  override def destroy(context:ServletContext) {
    super.destroy(context)
  }
}
