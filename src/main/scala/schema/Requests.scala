package schema

import schema.OperationType.OperationType

case class RegisterRequest(brokerAccountType: String) extends Schema

case class MarketOrderRequest(lots: Int, operation: OperationType) extends Schema
case class LimitOrderRequest(lots: Int, operation: OperationType, price: Double) extends Schema



case class SandboxSetPositionBalanceRequest(
  figi: String,
  balance: Double
) extends Schema
