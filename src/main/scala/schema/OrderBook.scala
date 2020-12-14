package schema

import schema.TradeStatus.TradeStatus

case class OrderBookResponse(status: String,
                             trackingId: String,
                             payload: OrderBook) extends Schema

case class OrderBook(figi: String,
                     depth: Int,
                     bids: Seq[OrderBResponse],
                     asks: Seq[OrderBResponse],
                     tradeStatus: TradeStatus,
                     minPriceIncrement: Double, //Шаг цены
                     faceValue: Option[Double], //Номинал для облигаций
                     lastPrice: Option[Double],
                     closePrice: Option[Double],
                     limitUp: Option[Double], //Верхняя граница цены
                     limitDown: Option[Double], //Нижняя граница цены
                    )

case class OrderBResponse(
                           price: Double,
                           quantity: Int
                         )