package telegram

import schema._
import storage.SLTPOrders

import scala.concurrent.Future

object MsgCreator {
  def notifyMsg(order: Option[SLTPOrders]): String = {
    order match {
      case None => "No new notification"
      case Some(o) => s"""OrderId: ${o.orderId}
                          MSG: ${o.message}""".stripMargin
    }
  }

  def message(response: Either[ErrorResponse, Schema]): Future[String] =
    response match {
      case Left(e) => Future.successful(s"Error: code: ${e.payload.code}, message: ${e.payload.message}")
      case Right(res) => res match {
        case PortfolioResponse(_, _, payload) => Future.successful(portfolioMsg(payload.positions))
        case PortfolioCurrenciesResponse(_, _, payload) => Future.successful(portfolioCurMsg(payload.currencies))
        case MarketInstrumentListResponse(_, _, payload) => Future.successful(marketInstrListMsg(payload))
        case MarketInstrumentListByFigiResponse(_,_,payload) => Future.successful(marketInstrumentMsg(payload))
        case OrderResponse(_, _, payload) => Future.successful(orderMsg(payload))
        case GetOrdersResponse(_, _, payload) => Future.successful(getOrderMsg(payload))
        case OrderBookResponse(_, _, payload) => Future.successful(getOrderBook(payload))
        case CandlesResponse(_, _, payload) => Future.successful(getCandleMsg(payload))
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
          marketInstrumentMsg(x)
        }.mkString("\n")
    else
      s"No instruments found for this ticket\n"
  }

  def marketInstrumentMsg(marketInstr: MarketInstrument): String =
    s"""Ticker: ${marketInstr.ticker}    Name: ${marketInstr.name}
       |Type: ${marketInstr.`type`}    FIGI: ${marketInstr.figi}
       |Lot: ${marketInstr.lot}    Minimum order quantity: ${marketInstr.minQuantity}
       |${pasteOrNot("Min Price Increment:", marketInstr.minPriceIncrement)}    ${pasteOrNot("Currency:", marketInstr.currency)}
       |______________""".stripMargin


  def orderMsg(order: PlacedOrder): String =
    s"""Order Id: ${order.orderId}
       |Status: ${order.status}
       |Operation: ${order.operation}
       |${pasteOrNot("Rejected Reason:", order.rejectReason)}    ${pasteOrNot("Message:", order.message)}
       |Requested Lots: ${order.requestedLots}    Executed Lots: ${order.executedLots}""".stripMargin


  def getOrderMsg(order: Seq[Order]): String = {
    if (order.isEmpty) s"No active orders"
    else
      order.map { x =>
        s"""Order Id: ${x.orderId}    FIGI: ${x.figi}
           |Operation: ${x.operation}    Status: ${x.status}
           |Requested Lots: ${x.requestedLots}    Executed Lots: ${x.executedLots}
           |Price: ${x.price}""".stripMargin
      }.mkString("\n")
  }

  def getOrderBook(order: OrderBook): String =
    s"""FIGI: ${order.figi} Depth: ${order.depth}
       |ASKS:
       |${order.asks.map { x => s"Price: ${x.price} Quantity: ${x.quantity}" }.mkString("\n")}
       |BIDS:
       |${order.bids.map { x => s"Price: ${x.price} Quantity: ${x.quantity}" }.mkString("\n")}
       |____________
       |Min Price Increment: ${order.minPriceIncrement}
       |${pasteOrNot("LastPrice:", order.lastPrice)}
       |${pasteOrNot("Close Price:", order.closePrice)}
       |${pasteOrNot("Limit Down:", order.limitDown)}
       |${pasteOrNot("Limit Up:", order.limitUp)}
       |""".stripMargin

  def getCandleMsg(candles: Candles): String = {
    s"FIGI ${candles.figi} Interval${candles.interval}\n" +
      candles.candles.map { x =>
        s"""Opening price: ${x.o}
           |Close price: ${x.c}
           |Highest price: ${x.h}
           |Lowest price: ${x.l}
           |Trading volume: ${x.v}
           |Time: ${x.time}
           |""".stripMargin
      }.mkString("\n")
  }

  def cancelOrderMsg(status: String): String =
    s"""Order canceled. Status: $status""".stripMargin


  def pasteOrNot(name: String, pos: Option[Any]): String =
    pos match {
      case Some(p) => s"$name $p"
      case None => ""
    }

}
