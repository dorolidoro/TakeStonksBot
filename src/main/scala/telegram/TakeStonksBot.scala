package telegram

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import canoe.api._
import canoe.models.ChatId
import canoe.models.messages.TelegramMessage
import canoe.syntax._
import cats.effect.{Async, ExitCode, IO, IOApp}
import cats.syntax.functor._
import com.typesafe.config.ConfigFactory
import fs2.Stream
import cats.effect.{Async, ContextShift, Sync}
import cats.instances.list._
import cats.syntax.flatMap._
import cats.syntax.foldable._
import cats.syntax.functor._
import schema.{ErrorResponse, OrderResponse}
import storage.DataBaseQuery

import scala.concurrent.{ExecutionContext, Future}
import service.{ServiceApi, ServiceWSApi}
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database
import storage.DataBaseQuery

import scala.concurrent.Future
import scala.util.Try

object TakeStonksBot extends IOApp {

  implicit val as: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = as.dispatcher
  implicit val materializer: ActorMaterializer.type = ActorMaterializer
  implicit val db: JdbcBackend.DatabaseDef = Database.forURL(
    ConfigFactory.load("application.conf").getString("db.url"),
    driver = ConfigFactory.load("application.conf").getString("db.driver"),
    keepAliveConnection = ConfigFactory.load("application.conf").getBoolean("db.keepAliveConnection")
  )

  val DBQuery = new DataBaseQuery

  val token: String = ConfigFactory.load("application.conf").getString("localhost.token")
  val tokenTF: String = ConfigFactory.load("application.conf").getString("TFApi.token")
  val rootUrl: String = ConfigFactory.load("application.conf").getString("TFApi.url")

  val service: ServiceApi.type = ServiceApi
  val serviceWs: ServiceWSApi.type = ServiceWSApi


  def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- Stream
        .resource(TelegramClient.global[IO](token))
        .flatMap { implicit client =>
          Bot.polling[IO]
            .follow(
              portfolio,
              balance,
              searchMarketInstrumentByTicket,
              searchMarketInstrumentByFigi,
              createMarketOrder,
              createLimitOrder,
              cancelOrder,
              //createStopLossTakeProfit,
              getOrderBook,
              //getCandles,
              orders,
              help
            )
        }
        .compile.drain.as(ExitCode.Success)

    } yield (ExitCode.Success)

  }

  def portfolio[F[_] : TelegramClient](implicit F: Async[F], cs: ContextShift[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("portfolio").chat)
      portfolioResp <- Scenario.eval(Async.fromFuture(F.delay(service.getPortfolio())))
      msg <- Scenario.eval(Async.fromFuture(F.delay(MsgCreator.message(portfolioResp))))
      _ <- Scenario.eval(chat.send(msg))
    } yield ()


  def balance[F[_] : TelegramClient](implicit F: Async[F], cs: ContextShift[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("balance").chat)
      portfolioResp <- Scenario.eval(Async.fromFuture(F.delay(service.getPortfolioCurrencies())))
      msg <- Scenario.eval(Async.fromFuture(F.delay(MsgCreator.message(portfolioResp))))
      _ <- Scenario.eval(chat.send(msg))
    } yield ()

  def searchMarketInstrumentByTicket[F[_] : TelegramClient](implicit F: Async[F], cs: ContextShift[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("searchbyticket").chat)
      _ <- Scenario.eval(chat.send("Please, enter the ticket:"))
      ticket <- Scenario.expect(text)
      marketInstrumentList <- Scenario.eval(Async.fromFuture(F.delay(service.searchMarketInstrumentByTicket(ticket))))
      msg <- Scenario.eval(Async.fromFuture(F.delay(MsgCreator.message(marketInstrumentList))))
      _ <- Scenario.eval(chat.send(msg))
    } yield ()

  def searchMarketInstrumentByFigi[F[_] : TelegramClient](implicit F: Async[F], cs: ContextShift[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("searchbyfigi").chat)
      _ <- Scenario.eval(chat.send("Please, enter the figi:"))
      figi <- Scenario.expect(text)
      marketInstrument <- Scenario.eval(Async.fromFuture(F.delay(service.searchMarketInstrumentByFIGI(figi))))
      msg <- Scenario.eval(Async.fromFuture(F.delay(MsgCreator.message(marketInstrument))))
      _ <- Scenario.eval(chat.send(msg))
    } yield ()

  def createMarketOrder[F[_] : TelegramClient](implicit F: Async[F], cs: ContextShift[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("marketorder").chat)
      _ <- Scenario.eval(chat.send(
        """Please, enter the figi, the number of lots and operation (Buy or Sell), separated by space.
          |For example: SB2233938 1 Buy""".stripMargin))
      enter <- Scenario.expect(text)
      orderResp <- Scenario.eval(Async.fromFuture(F.delay(
        service.createMarketOrder(
          figi = enter.split(" ")(0),
          lots = Try(enter.split(" ")(1).toInt).getOrElse(0),
          operation = enter.split(" ")(2)))))
      msg <- Scenario.eval(Async.fromFuture(F.delay(MsgCreator.message(orderResp))))
      _ <- Scenario.eval(chat.send(msg))
    } yield ()


  def createLimitOrder[F[_] : TelegramClient](implicit F: Async[F], cs: ContextShift[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("limitorder").chat)
      _ <- Scenario.eval(chat.send(
        """Please, enter the figi, the number of lots, operation (Buy or Sell), price separated by space.
          |For example: SB2233938 1 Buy 120.80""".stripMargin))
      enter <- Scenario.expect(text)
      orderResp <- Scenario.eval(Async.fromFuture(F.delay(
        service.createLimitOrder(
          figi = enter.split(" ")(0),
          lots = enter.split(" ")(1).toInt, //try
          operation = enter.split(" ")(2),
          price = enter.split(" ")(3).toDouble))))
      msg <- Scenario.eval(Async.fromFuture(F.delay(MsgCreator.message(orderResp))))
      _ <- Scenario.eval(chat.send(msg))
    } yield ()

  def cancelOrder[F[_] : TelegramClient](implicit F: Async[F], cs: ContextShift[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("cancelorder").chat)
      _ <- Scenario.eval(chat.send("Please, enter the order Id:"))
      id <- Scenario.expect(text)
      portfolioResp <- Scenario.eval(Async.fromFuture(F.delay(service.cancelOrder(id))))
      msg <- Scenario.eval(Async.fromFuture(F.delay(MsgCreator.message(portfolioResp))))
      _ <- Scenario.eval(chat.send(msg))
    } yield ()

  def orders[F[_] : TelegramClient](implicit F: Async[F], cs: ContextShift[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("orders").chat)
      orders <- Scenario.eval(Async.fromFuture(F.delay(service.getOrders())))
      msg <- Scenario.eval(Async.fromFuture(F.delay(MsgCreator.message(orders))))
      _ <- Scenario.eval(chat.send(msg))
    } yield ()

  def getOrderBook[F[_] : TelegramClient](implicit F: Async[F], cs: ContextShift[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("orderbook").chat)
      _ <- Scenario.eval(chat.send(
        """Please, enter figi and depth (1 .. 20)
          |For example: SB2233938 1""".stripMargin))
      enter <- Scenario.expect(text)
      orderBookResp <- Scenario.eval(Async.fromFuture(F.delay(service.getOrderBook(enter.split(" ")(0),
        enter.split(" ")(1).toIntOption.getOrElse(-1)))))
      msg <- Scenario.eval(Async.fromFuture(F.delay(MsgCreator.message(orderBookResp))))
      _ <- Scenario.eval(chat.send(msg))
    } yield ()

  def getCandles[F[_] : TelegramClient](implicit F: Async[F], cs: ContextShift[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("candles").chat)
      _ <- Scenario.eval(chat.send(
        """Please, enter figi, interval (1min, 2min, day, etc.), from, to
          |For example: SB2233938 day 2020-12-11T06:38:33.131642+03:00 2020-12-11T19:38:33.131642+03:00""".stripMargin))
      enter <- Scenario.expect(text)
      candleResp <- Scenario.eval(Async.fromFuture(F.delay(service.getCandles(enter.split(" ")(0),
        enter.split(" ")(1), enter.split(" ")(2), enter.split(" ")(3)))))
      msg <- Scenario.eval(Async.fromFuture(F.delay(MsgCreator.message(candleResp))))
      _ <- Scenario.eval(chat.send(msg))
    } yield ()


  def createStopLossTakeProfit[F[_] : TelegramClient](implicit F: Async[F], cs: ContextShift[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("stoplosstakeprofit").chat)
      _ <- Scenario.eval(chat.send("Please, enter figi, lots, StopLoss price and TakeProfit price:"))
      enter <- Scenario.expect(text)
      _ <- Scenario.eval(Async.fromFuture(F.delay(serviceWs.makeSLTPOrder(parseStopLossTakeProfit(enter)))))
    } yield ()

  case class StopLossTakeProfitOrder(figi: String, lots: Option[Int], price1: Option[Double], price2: Option[Double])

  def parseStopLossTakeProfit(text: String) = {
    val parsed = text.split(" ")
    parsed.length match {
      case 4 => StopLossTakeProfitOrder(parsed(0),
        parsed(1).toIntOption,
        parsed(2).toDoubleOption,
        parsed(3).toDoubleOption)
      case 3 => StopLossTakeProfitOrder(parsed(0),
        parsed(1).toIntOption,
        parsed(2).toDoubleOption,
        None)
      case _ => StopLossTakeProfitOrder("Error", None, None, None)
    }
  }

  def notifySLTP[F[_] : TelegramClient](implicit F: Async[F], cs: ContextShift[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("notify").chat)
      msgSL <- Scenario.eval(Async.fromFuture(F.delay(db.run(DBQuery.findOrdersByTypeAndStatus("SL", "New")).andThen { case _ => db.close() })))
      msgTP <- Scenario.eval(Async.fromFuture(F.delay(db.run(DBQuery.findOrdersByTypeAndStatus("TP", "New")).andThen { case _ => db.close() })))
      //_ <- Scenario.eval(chat.send("StopLoss:"))
      _ <- Scenario.eval(chat.send("StopLoss: \n" + MsgCreator.notifyMsg(msgSL)))
      _ <- Scenario.eval(chat.send("TakeProfit: \n" + MsgCreator.notifyMsg(msgTP)))

      _ <- Scenario.eval(Async.fromFuture(F.delay {
        msgSL match {
          case None => Future.successful(None)
          case Some(x) => db.run(DBQuery.updOrderType(x.orderId)).andThen { case _ => db.close() }
        }
      }))
      _ <- Scenario.eval(Async.fromFuture(F.delay {
        msgTP match {
          case None => Future.successful(None)
          case Some(x) => db.run(DBQuery.updOrderType(x.orderId)).andThen { case _ => db.close() }
        }
      }))

    } yield ()


  ////
  def help[F[_] : TelegramClient]: Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("help").chat)
      _ <- Scenario.eval(chat.send(
        s"""Commands:
           |/portfolio - shows your portfolio
           |/balance - shows your currencies balance
           |/searchbyticket - searches market's instruments by ticket
           |/searchbyfigi - searches market's instruments by figi
           |/orderbook - shows orderbook by figi
           |/marketorder - create market order by figi
           |/limitorder - create limit order by figi
           |/orders - shows your active orders
           |/cancelorder - cancel order by id
           |/notify - shows yours executed SLTP
           |/help - show all commands
           |""".stripMargin
      )) // /stoplosstakeprofit - create stoploss takeprofit order
      ///candles - shows candles
    } yield ()

  //  def push[F[_] : TelegramClient](sltpRes: Future[Either[ErrorResponse, OrderResponse]])(implicit F: Async[F], cs: ContextShift[F]): Scenario[F, Unit] ={
  //    for{
  //      chat <- Scenario.expect(command("").chat)
  //      sltp <- sltpRes
  //      msg <- Scenario.eval(Async.fromFuture(F.delay(MsgCreator.message(sltp))))
  //      _ <- Scenario.eval(chat.send(msg))
  //    } yield ()
  //  }


}
