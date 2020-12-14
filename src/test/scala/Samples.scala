import schema._

object Samples {


  val portfolio: PortfolioResponse = PortfolioResponse("6cc4e8a98859bb38",
    "Ok",
    Portfolio(List(
      PortfolioPosition("BBG000B9XRY4", "AAPL", Some("US0378331005"), "Stock", 9995000.0, Some(0.0), None, 9995000, None, None, "Apple"),
      PortfolioPosition("BBG0013HGFT4", "USD000UTSTOM", None, "Currency", 499900.0, Some(0.0), None, 499, None, None, "Доллар США"))))

  val portfolioCur: PortfolioCurrenciesResponse = PortfolioCurrenciesResponse("91288e11a332f32f", "Ok",
    Currencies(List(CurrencyPosition("EUR", 0.0, None), CurrencyPosition("RUB", 0.0, None), CurrencyPosition("USD", 500300.0, None))))

  val order: OrderResponse = OrderResponse("45c2903462aee755", "Ok",
    PlacedOrder("6d4a60d4-1834-4113-be2c-ada96f432481", "Sell", "Fill", None, None, 1, 1, None))

  val getOrders: GetOrdersResponse = GetOrdersResponse("bb14ea76505e846a", "Ok",
    List(Order("6d4a60d4-1834-4113-be2c-ada96f432481", "BBG000B9XRY4", "Buy", "New", 1, 1, 100.0)))

  val marketInstrumentList: MarketInstrumentListResponse = MarketInstrumentListResponse("c8c0fa7175f75ff5", "Ok",
    MarketInstrumentList(Some(1), List(MarketInstrument("BBG000B9XRY4", "AAPL", "US0378331005", Some(0.01), 1, None, Some("USD"), "Apple", "Stock"))))

  val error: ErrorResponse = ErrorResponse("23cca5955990dc41", "Error", PayloadError("Cannot find route", "UNDEFINED_ROUTE"))

  val orderBook: OrderBookResponse = OrderBookResponse("Ok","de467bb39a1659a8",OrderBook("BBG000B9XRY4",1,
    List(OrderBResponse(122.45,245)),
    List(OrderBResponse(122.48,230)),
    "NormalTrading",0.01,None,Some(122.44),Some(122.41),Some(124.22),Some(121.01)))



  val portfolioRes: Either[ErrorResponse, PortfolioResponse] = Right(portfolio).withLeft[ErrorResponse]

  val orderRes: Either[ErrorResponse, OrderResponse] = Right(order).withLeft[ErrorResponse]

  val portfolioCurRes: Either[ErrorResponse, PortfolioCurrenciesResponse] = Right(portfolioCur).withLeft[ErrorResponse]

  val getOrdersRes: Either[ErrorResponse, GetOrdersResponse] = Right(getOrders).withLeft[ErrorResponse]

  val searchByTicketRes: Either[ErrorResponse, MarketInstrumentListResponse] = Right(marketInstrumentList).withLeft[ErrorResponse]

  val orderBookRes: Either[ErrorResponse, OrderBookResponse] = Right(orderBook).withLeft[ErrorResponse]

  val errorRes: Either[ErrorResponse, EmptyResponse] = Left(error).withRight[EmptyResponse]


  val portfolioExpectMsg: String = portfolio.payload.positions.map { x =>
    s"""Ticker: ${x.ticker}    Name: ${x.name}
       |Type: ${x.instrumentType}    FIGI: ${x.figi}
       |Lots: ${x.lots}    Balance: ${x.balance}
       |______________""".stripMargin
  }.mkString("\n")

  val portfolioCurExpectMsg: String = portfolioCur.payload.currencies.map { x =>
    s"""currency: ${x.currency}
       |balance: ${x.balance}
       |
       |______________""".stripMargin
  }.mkString("\n")

  val orderExpectMsg: String =
    s"""Order Id: ${order.payload.orderId}
       |Status: ${order.payload.status}
       |Operation: ${order.payload.operation}
       |${s"    "}
       |Requested Lots: ${order.payload.requestedLots}    Executed Lots: ${order.payload.executedLots}""".stripMargin

  val getOrdersExpectMsg: String = getOrders.payload.map { x =>
    s"""Order Id: ${x.orderId}    FIGI: ${x.figi}
       |Operation: ${x.operation}    Status: ${x.status}
       |Requested Lots: ${x.requestedLots}    Executed Lots: ${x.executedLots}
       |Price: ${x.price}""".stripMargin
  }.mkString("\n")

  val searchByTicketsExpectMsg: String = s"Find ${marketInstrumentList.payload.total.getOrElse(0)} instrument(s)\n" +
    marketInstrumentList.payload.instruments.map { x =>
      s"""Ticker: ${x.ticker}    Name: ${x.name}
         |Type: ${x.`type`}    FIGI: ${x.figi}
         |Lot: ${x.lot}    Minimum order quantity: ${x.minQuantity}
         |Min Price Increment: ${x.minPriceIncrement}    Currency: ${x.currency}
         |______________""".stripMargin
    }.mkString("\n")

  val orderBookExpectMsg: String = s"""FIGI: ${orderBook.payload.figi} Depth: ${orderBook.payload.depth}
                                      |ASKS:
                                      |Price: ${orderBook.payload.asks.headOption.getOrElse(OrderBResponse(0,0)).price} Quantity: ${orderBook.payload.asks.headOption.getOrElse(OrderBResponse(0,0)).quantity}
                                      |BIDS:
                                      |Price: ${orderBook.payload.bids.headOption.getOrElse(OrderBResponse(0,0)).price} Quantity: ${orderBook.payload.bids.headOption.getOrElse(OrderBResponse(0,0)).quantity}
                                      |____________
                                      |Min Price Increment: ${orderBook.payload.minPriceIncrement}
                                      |LastPrice: ${orderBook.payload.lastPrice.getOrElse(0)}
                                      |Close Price: ${orderBook.payload.closePrice.getOrElse(0)}
                                      |Limit Down: ${orderBook.payload.limitDown.getOrElse(0)}
                                      |Limit Up: ${orderBook.payload.limitUp.getOrElse(0)}
                                      |""".stripMargin

  val errorExpectMsg = s"Error: code: ${error.payload.code}, message: ${error.payload.message}"
}
