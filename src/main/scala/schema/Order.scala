package schema

import schema.OperationType.OperationType
import schema.OrderStatus.OrderStatus

case class OrderResponse(trackingId: String, status: String, payload: PlacedOrder) extends Schema

case class PlacedOrder(orderId: String,
                       operation: OperationType,
                       status: OrderStatus,
                       rejectReason: Option[String],
                       message: Option[String],
                       requestedLots: Int,
                       executedLots: Int,
                       commission: Option[MoneyAmount]) extends Schema

case class GetOrdersResponse(trackingId: String, status: String, payload: Seq[Order]) extends Schema

case class Order(orderId: String,
                 figi: String,
                 operation: OperationType,
                 status: OrderStatus,
                 requestedLots: Int,
                 executedLots: Int,
                 price: Double)

