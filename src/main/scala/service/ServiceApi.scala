package service

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.syntax._
import schema.CandleResolution.CandleResolution
import schema._

import scala.concurrent.Future

object ServiceApi {

  import telegram.TakeStonksBot._

  val auth: Authorization = headers.Authorization(OAuth2BearerToken(tokenTF))
  val request: HttpRequest = HttpRequest(
    uri = rootUrl,
    headers = Seq(auth)
  )

  //for sandbox only
  def register: Future[Either[ErrorResponse, RegisterResponse]] = {
    Http().singleRequest(request
      .withMethod(POST)
      .withUri(request.uri + "/sandbox/register")
      .withEntity(HttpEntity(ContentTypes.`application/json`, RegisterRequest("Tinkoff").asJson.noSpaces))).flatMap(res =>
      res.status match {
        case StatusCodes.OK =>
          Unmarshal(res).to[RegisterResponse].map(Right(_).withLeft[ErrorResponse])
        case _ =>
          Unmarshal(res).to[ErrorResponse].map(Left(_).withRight[RegisterResponse])
      })
  }

  def setCurrenciesBalance(brokerAccountId: String, balanceCurr: MoneyAmount): Future[Either[ErrorResponse, EmptyResponse]] = {
    Http().singleRequest(request
      .withMethod(POST)
      .withUri(Uri(request.uri + "/sandbox/currencies/balance").withQuery(Query(Map("brokerAccountId" -> brokerAccountId))))
      .withEntity(HttpEntity(ContentTypes.`application/json`, balanceCurr.asJson.noSpaces))).flatMap(res =>
      res.status match {
        case StatusCodes.OK =>
          Unmarshal(res).to[EmptyResponse].map(Right(_).withLeft[ErrorResponse])
        case _ =>
          Unmarshal(res).to[ErrorResponse].map(Left(_).withRight[EmptyResponse])
      })
  }

  def setPositionBalance(balancePos: SandboxSetPositionBalanceRequest): Future[Either[ErrorResponse, EmptyResponse]] = {
    Http().singleRequest(request
      .withMethod(POST)
      .withUri(request.uri + "/sandbox/positions/balance")
      .withEntity(HttpEntity(ContentTypes.`application/json`, balancePos.asJson.noSpaces))).flatMap(res =>
      res.status match {
        case StatusCodes.OK =>
          Unmarshal(res).to[EmptyResponse].map(Right(_).withLeft[ErrorResponse])
        case _ =>
          Unmarshal(res).to[ErrorResponse].map(Left(_).withRight[EmptyResponse])
      })
  }

  /////


  def getPortfolio(): Future[Either[ErrorResponse, PortfolioResponse]] = {
    Http().singleRequest(request
      .withMethod(GET)
      .withUri(Uri(request.uri + "/portfolio"))) //.withQuery(Query(Map("brokerAccountId" -> brokerAccountId)))))
      .flatMap(res =>
        res.status match {
          case StatusCodes.OK =>
            Unmarshal(res).to[PortfolioResponse].map(Right(_).withLeft[ErrorResponse])
          case _ =>
            Unmarshal(res).to[ErrorResponse].map(Left(_).withRight[PortfolioResponse])
        })
  }

  def getPortfolioCurrencies(): Future[Either[ErrorResponse, PortfolioCurrenciesResponse]] = {
    Http().singleRequest(request
      .withMethod(GET)
      .withUri(Uri(request.uri + "/portfolio/currencies")))
      .flatMap(res =>
        res.status match {
          case StatusCodes.OK =>
            Unmarshal(res).to[PortfolioCurrenciesResponse].map(Right(_).withLeft[ErrorResponse])
          case _ =>
            Unmarshal(res).to[ErrorResponse].map(Left(_).withRight[PortfolioCurrenciesResponse])
        })
  }

  def searchMarketInstrumentByTicket(ticker: String): Future[Either[ErrorResponse, MarketInstrumentListResponse]] = {
    Http().singleRequest(request
      .withMethod(GET)
      .withUri(Uri(request.uri + "/market/search/by-ticker").withQuery(Query(Map("ticker" -> ticker)))))
      .flatMap(res =>
        res.status match {
          case StatusCodes.OK =>
            Unmarshal(res).to[MarketInstrumentListResponse].map(Right(_).withLeft[ErrorResponse])
          case _ =>
            Unmarshal(res).to[ErrorResponse].map(Left(_).withRight[MarketInstrumentListResponse])
        })
  }

  def searchMarketInstrumentByFIGI(figi: String): Future[Either[ErrorResponse, MarketInstrumentListByFigiResponse]] = {
    Http().singleRequest(request
      .withMethod(GET)
      .withUri(Uri(request.uri + "/market/search/by-figi").withQuery(Query(Map("figi" -> figi)))))
      .flatMap(res =>
        res.status match {
          case StatusCodes.OK =>
            Unmarshal(res).to[MarketInstrumentListByFigiResponse].map(Right(_).withLeft[ErrorResponse])
          case _ =>
            Unmarshal(res).to[ErrorResponse].map(Left(_).withRight[MarketInstrumentListByFigiResponse])
        })
  }

  def createMarketOrder(figi: String, lots: Int, operation: String): Future[Either[ErrorResponse, OrderResponse]] = {
    Http().singleRequest(request
      .withMethod(POST)
      .withUri(Uri(request.uri + "/orders/market-order").withQuery(Query(Map("figi" -> figi))))
      .withEntity(HttpEntity(ContentTypes.`application/json`, MarketOrderRequest(lots, operation).asJson.noSpaces)))
      .flatMap(res =>
        res.status match {
          case StatusCodes.OK =>
            Unmarshal(res).to[OrderResponse].map(Right(_).withLeft[ErrorResponse])
          case _ =>
            Unmarshal(res).to[ErrorResponse].map(Left(_).withRight[OrderResponse])
        })
  }

  def createLimitOrder(figi: String, lots: Int, operation: String, price: Double): Future[Either[ErrorResponse, OrderResponse]] = {
    Http().singleRequest(request
      .withMethod(POST)
      .withUri(Uri(request.uri + "/orders/market-order").withQuery(Query(Map("figi" -> figi))))
      .withEntity(HttpEntity(ContentTypes.`application/json`, LimitOrderRequest(lots, operation, price).asJson.noSpaces)))
      .flatMap(res =>
        res.status match {
          case StatusCodes.OK =>
            Unmarshal(res).to[OrderResponse].map(Right(_).withLeft[ErrorResponse])
          case _ =>
            Unmarshal(res).to[ErrorResponse].map(Left(_).withRight[OrderResponse])
        })
  }

  def getOrders(): Future[Either[ErrorResponse, GetOrdersResponse]] = {
    Http().singleRequest(request
      .withMethod(GET)
      .withUri(Uri(request.uri + "/orders")))
      .flatMap(res =>
        res.status match {
          case StatusCodes.OK =>
            Unmarshal(res).to[GetOrdersResponse].map(Right(_).withLeft[ErrorResponse])
          case _ =>
            Unmarshal(res).to[ErrorResponse].map(Left(_).withRight[GetOrdersResponse])
        })
  }

  def cancelOrder(orderId: String): Future[Either[ErrorResponse, EmptyResponse]] = {
    Http().singleRequest(request
      .withMethod(POST)
      .withUri(Uri(request.uri + "/orders/cancel").withQuery(Query(Map("orderId" -> orderId)))))
      .flatMap(res =>
        res.status match {
          case StatusCodes.OK =>
            Unmarshal(res).to[EmptyResponse].map(Right(_).withLeft[ErrorResponse])
          case _ =>
            Unmarshal(res).to[ErrorResponse].map(Left(_).withRight[EmptyResponse])
        })
  }

  def getOrderBook(figi: String, depth: Int): Future[Either[ErrorResponse, OrderBookResponse]] = {
    Http().singleRequest(request
      .withMethod(GET)
      .withUri(Uri(request.uri + s"/market/orderbook?figi=$figi&depth=$depth"))).flatMap(res =>
      res.status match {
        case StatusCodes.OK =>
          Unmarshal(res).to[OrderBookResponse].map(Right(_).withLeft[ErrorResponse])
        case _ =>
          Unmarshal(res).to[ErrorResponse].map(Left(_).withRight[OrderBookResponse])
      })
  }


  def getCandles(figi: String, interval: CandleResolution, from: String, to: String):
  Future[Either[ErrorResponse, CandlesResponse]] = {
    Http().singleRequest(request
      .withMethod(GET)
      .withUri(Uri(request.uri + s"/market/candles?figi=$figi&interval=$interval&from=$from&to=$to"))).flatMap(res =>
      res.status match {
        case StatusCodes.OK =>
          Unmarshal(res).to[CandlesResponse].map(Right(_).withLeft[ErrorResponse])
        case _ =>
          Unmarshal(res).to[ErrorResponse].map(Left(_).withRight[CandlesResponse])
      })
  }
}

