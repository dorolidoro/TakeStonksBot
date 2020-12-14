package storage

import com.typesafe.config.ConfigFactory
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database
import telegram.TakeStonksBot.ec


case class SLTPOrders(
                       orderId: String,
                       `type`: String,
                       price: Double,
                       status: String,
                       message: String

                     )

class SLTPOrdersTable(tag: Tag) extends Table[SLTPOrders](tag, "SLTP_ORDERS") {

  def orderId: Rep[String] = column("ORDER_ID", O.PrimaryKey)
  def price: Rep[Double] = column("PRICE")

  def `type`: Rep[String] = column("TYPE")

  def status: Rep[String] = column("STATUS")
  def message: Rep[String] = column("MESSAGE")

  override def * : ProvenShape[SLTPOrders] = ( orderId, `type`, price, status, message).mapTo[SLTPOrders]
}

case class Users(
                  chatId: String,
                  token: String,
                  brokerAccountId: String
                )

class UsersTable(tag: Tag) extends Table[Users](tag, "USERS") {

  def chatId: Rep[String] = column("CHAT_ID", O.PrimaryKey)

  def token: Rep[String] = column("TOKEN")

  def brokerAccountId: Rep[String] = column("BROKER_ACCOUNT_ID")

  override def * : ProvenShape[Users] = (chatId, token, brokerAccountId).mapTo[Users]
}

class DataBaseQuery {
  val AllSLTPOrders = TableQuery[SLTPOrdersTable]
  val AllUsers = TableQuery[UsersTable]

  def exec[R, S <: NoStream, E <: Effect](fun: DBIOAction[R, S, E])
                                         (implicit db: JdbcBackend.DatabaseDef): Unit = {
    db.run(fun)
      .andThen { case _ => db.close() }
  }

  def findUser(chatId: String): DIO[Option[Users], Effect.Read] =
    AllUsers
      .filter(_.chatId === chatId)
      .result
      .headOption

  def addUser(user: Users): DIO[Int, Effect.Write] =
    AllUsers += user

  def findSLTPOrders(orderId: String): DIO[Option[SLTPOrders], Effect.Read] =
    AllSLTPOrders
      .filter(_.orderId === orderId)
      .result
      .headOption

  def addSLTPOrders(SLTPOrders: SLTPOrders): DIO[Int, Effect.Write] =
    AllSLTPOrders += SLTPOrders

  def findOrdersByTypeAndStatus(`type`: String, status: String)(implicit ec: ExecutionContext):
  DIO[Option[SLTPOrders], Effect.Read] = {
    AllSLTPOrders
      .filter(_.`type` === `type`)
      .filter(_.status === status)
      .result
      .headOption
  }

  def updOrderType(orderId: String): DIO[Int, Effect.Write] = {
    val q = for {elem <- AllSLTPOrders if elem.orderId === orderId} yield elem.`type`
    q.update("Notified")
  }
}


