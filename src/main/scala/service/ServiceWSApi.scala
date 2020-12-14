package service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.{Done, NotUsed}
import akka.http.scaladsl.model.headers
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import schema.websocket.{CandleRequest, CandleResponse}
import io.circe.syntax._
import io.circe.generic.auto._
import schema.{ErrorResponse, LimitOrderRequest, OperationType, OrderResponse}

import scala.concurrent.{ExecutionContext, Future}
import schema.websocket._
import storage.SLTPOrders
import telegram.{MsgCreator, TakeStonksBot}


object ServiceWSApi {
  import telegram.TakeStonksBot._
  val authReq: WebSocketRequest = WebSocketRequest(
    "wss://api-invest.tinkoff.ru/openapi/md/v1/md-openapi/ws",
    extraHeaders = Seq(headers.Authorization(OAuth2BearerToken(tokenTF))))


  def candleSubscribe(figi: String, lots: Int, stopLoss: Double, takeProfit: Double, interval: String) = {
    val eventSink: Sink[Message, Future[Done]] =
      Sink.foreach {
        case message: TextMessage.Strict => {
          val candle = Unmarshal(message.text).to[CandleResponse]
          candle.map(candle => {
            if (candle.payload.c <= stopLoss) {
              for {
                order <- service.createLimitOrder(figi, lots, OperationType.Sell, stopLoss)
                msg <- MsgCreator.message(order)
              } yield (stopLossTakeProfitEvent(order, candle.payload.c, msg, "SL"))
            }
            if (candle.payload.c >= takeProfit) {
              for {
                order <- service.createLimitOrder(figi, lots, OperationType.Sell, takeProfit)
                msg <- MsgCreator.message(order)
              } yield (stopLossTakeProfitEvent(order, candle.payload.c, msg, "TP"))
            }
          })
        }
        case _ =>
        // ignore other message types
      }

    val candleSource: Source[Message, NotUsed] = {
      Source.single(TextMessage(CandleRequest("candle:subscribe", figi, interval).asJson.noSpaces))
    }

    val flow: Flow[Message, Message, Future[Done]] =
    Flow.fromSinkAndSourceMat(eventSink, candleSource)(Keep.left)

    val (upgradeResponse, closed) = Http().singleWebSocketRequest(authReq, flow)

  }

//  def eventStopLoss(stopLoss: Double, currPrice: Double) =
//  def eventTakeProfit = ???

  def makeSLTPOrder(order: StopLossTakeProfitOrder) = {
    order match {
      case StopLossTakeProfitOrder(figi,  Some(lots),  Some(sl),  Some(tp)) => Future.successful(candleSubscribe(figi, lots, sl, tp, "1min"))
//      case sltp@StopLossTakeProfitOrder(_, Some(_),  Some(_), _) =>
      case _ => Future.successful("Wrong parameters")
    }
  }


  def stopLossTakeProfitEvent(order: Either[ErrorResponse, OrderResponse], price: Double, message: String, `type`: String): Unit = {
    order match {
      case Left(e) =>
      case Right(value) => db.run(DBQuery.addSLTPOrders(SLTPOrders(value.payload.orderId,
        `type`, price, "New", message))).andThen { case _ => db.close() }
    }
  }



//  def checkStopLossTakeProfit(order: StopLossTakeProfitOrder): Future[Either[String, Option[Double]]] = {
//    for{
//      orderBook <- service.getOrderBook(order.figi, 1)
//      res = orderBook match {
//        case Left(e) => s"Error: code: ${e.payload.code}, message: ${e.payload.message}"
//        case Right(res) => if (order.price1.getOrElse() >= res.payload.lastPrice.getOrElse())
//      }
//    } yield(res)
//
//  }




}
