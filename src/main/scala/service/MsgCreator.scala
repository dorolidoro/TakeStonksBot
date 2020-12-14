package service

import scala.concurrent.Future
import schema._

object MsgCreator {
  def message(response: Either[ErrorResponse, Schema]): Future[String] =
    response match {
      case Left(e) => Future.successful(s"Error: code: ${e.payload.code}, message: ${e.payload.message}")
      case Right(res) => res match {
        case PortfolioResponse(_, _, payload) => Future.successful(portfolioMsg(payload.positions))
        case PortfolioCurrenciesResponse(_, _, payload) => Future.successful(portfolioCurMsg(payload.currencies))
        case MarketInstrumentListResponse(_, _, payload) => Future.successful(marketInstrListMsg(payload))
        case OrderResponse(_, _, payload) => Future.successful(orderMsg(payload))
        case GetOrdersResponse(_, _, payload) => Future.successful(getOrderMsg(payload))
        case OrderBookResponse(_, _, payload) => Future.successful(getOrderBook(payload))
        case EmptyResponse(_, status, _) => Future.successful(cancelOrderMsg(status))
      }
    }

  def portfolioMsg(positions: Seq[PortfolioPosition]): String =
    if (positions.nonEmpty)
      positions.map { x =>
        s"""Ticker: ${x.ticker}    Name: ${x.name}
           |Type: ${x.instrumentType}    FIGI: ${x.figi}
           |Lots: ${x.lots}    Balance: ${x.balance}
           |______________""".stripMargin
      }.mkString("\n")
    else s"Your portfolio is empty"

  def portfolioCurMsg(currencies: Seq[CurrencyPosition]): String =
    if (currencies.nonEmpty)
      currencies.map { x =>
        s"""currency: ${x.currency}
           |balance: ${x.balance}
           |${pasteOrNot("Blocked:", x.blocked)}
           |______________""".stripMargin
      }.mkString("\n")
    else s"You don't have any currencies"

  def marketInstrListMsg(marketInstrList: MarketInstrumentList): String = {
    val total = marketInstrList.total.getOrElse(0)
    if (total > 0)
      s"Find ${total} instrument(s)\n" +
        marketInstrList.instruments.map { x =>
          s"""Ticker: ${x.ticker}    Name: ${x.name}
             |Type: ${x.`type`}    FIGI: ${x.figi}
             |Lot: ${x.lot}    Minimum order quantity: ${x.minQuantity}
             |Min Price Increment: ${x.minPriceIncrement}    Currency: ${x.currency}
             |______________""".stripMargin
        }.mkString("\n")
    else
      s"No instruments found for this ticket\n"
  }

  def orderMsg(order: PlacedOrder): String =
    s"""Order Id: ${order.orderId}
       |Status: ${order.status}
       |Operation: ${order.operation}
       |${pasteOrNot("Rejected Reason:", order.rejectReason)}    ${pasteOrNot("Message:", order.message)}
       |Requested Lots: ${order.requestedLots}    Executed Lots: ${order.executedLots}""".stripMargin


  def getOrderMsg(order: Seq[Order]): String =
    order.map{ x =>
      s"""Order Id: ${x.orderId}    FIGI: ${x.figi}
         |Operation: ${x.operation}    Status: ${x.status}
         |Requested Lots: ${x.requestedLots}    Executed Lots: ${x.executedLots}
         |Price: ${x.price}""".stripMargin
  }.mkString("\n")

  def getOrderBook(order: OrderBook): String =
    s"""FIGI: ${order.figi} Depth: ${order.depth}
       |ASKS:
       |${order.asks.map{x => s"Price: ${x.price} Quantity: ${x.quantity}"}.mkString("\n")}
       |BIDS:
       |${order.bids.map{x => s"Price: ${x.price} Quantity: ${x.quantity}"}.mkString("\n")}
       |____________
       |Min Price Increment: ${order.minPriceIncrement}
       |${pasteOrNot("LastPrice:", order.lastPrice)}
       |${pasteOrNot("Close Price:", order.closePrice)}
       |${pasteOrNot("Limit Down:", order.limitDown)}
       |${pasteOrNot("Limit Up:", order.limitUp)}
       |""".stripMargin

  def cancelOrderMsg(status: String): String =
      s"""Order canceled. Status: $status""".stripMargin


  def pasteOrNot(name: String, pos: Option[Any]): String =
    pos match {
      case Some(p) => s"$name $p"
      case None => ""
    }

}
