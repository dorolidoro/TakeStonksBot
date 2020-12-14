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
                       brokerAccountId: String,
                       orderId: String,
                       status: String
                     )

class SLTPOrdersTable(tag: Tag) extends Table[SLTPOrders](tag, "SLTP_ORDERS") {

  def brokerAccountId: Rep[String] = column("BROKER_ACCOUNT_ID")

  def orderId: Rep[String] = column("ORDER_ID", O.PrimaryKey)

  def status: Rep[String] = column("STATUS")

  override def * : ProvenShape[SLTPOrders] = (brokerAccountId, orderId, status).mapTo[SLTPOrders]
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

  def findUserOrders(brokerAccountId: String)(implicit ec: ExecutionContext):
  DBIOAction[Map[Users, Seq[SLTPOrders]], NoStream, Effect.Read] = {
    AllSLTPOrders.filter(_.brokerAccountId === brokerAccountId)
      .join(AllUsers)
      .on(_.brokerAccountId === _.brokerAccountId)
      .result
      .map(_.groupMap(_._2)(_._1))


  }
}


