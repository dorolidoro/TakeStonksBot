import com.typesafe.config.ConfigFactory
import org.scalactic.source
import org.scalatest.compatible
import org.scalatest.funsuite.AsyncFunSuite
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database
import storage.{SLTPOrders, Users}
//import entrypoint.Main._


abstract class DatabaseSuite extends AsyncFunSuite {
  protected def test[R, S <: NoStream, E <: Effect](testName: String)
                                                   (testFun: DBIOAction[compatible.Assertion, S, E])
                                                   (implicit pos: source.Position): Unit = {

    super.test(testName) {
      //не видит ec из Main

      val db: JdbcBackend.DatabaseDef = Database.forURL(
        ConfigFactory.load("application.conf").getString("db.url"),
        driver = ConfigFactory.load("application.conf").getString("db.driver"),
        keepAliveConnection = ConfigFactory.load("application.conf").getBoolean("db.keepAliveConnection")
      )

       db.run(testFun)
        .andThen { case _ => db.close() }
    }
  }

  protected val SampleUsers = Seq(
    Users("1", "token", "tf_id_1")
  )

  protected val SampleOrders = Seq(
    SLTPOrders ("1", "SL", 100, "New", "Order complete")
  )
}
