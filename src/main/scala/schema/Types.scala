package schema

object Currency {
  type Currency = String
  val RUB = "RUB"
  val USD = "USD"
  val EUR = "EUR"
  val GBP = "GBP"
  val HKD = "HKD"
  val CHF = "CHF"
  val JPY = "JPY"
  val CNY = "CNY"
  val TRY = "TRY"
}

object OperationType {
  type OperationType = String
  val Buy = "Buy"
  val Sell = "Sell"
}

object OrderType {
  type OrderType = String
  val Limit = "Limit"
  val Market = "Market"
}

object OrderStatus {
  type OrderStatus = String
  val New = "New"
  val PartiallyFill = "PartiallyFill"
  val Fill = "Fill"
  val Cancelled = "Cancelled"
  val Replaced = "Replaced"
  val PendingCancel = "PendingCancel"
  val Rejected = "Rejected"
  val PendingReplace = "PendingReplace"
  val PendingNew = "PendingNew"
}

object TradeStatus {
  type TradeStatus = String
  val NormalTrading = "NormalTrading"
  val NotAvailableForTrading = "NotAvailableForTrading"
}

object CandleResolution {
  type CandleResolution = String
  val `1min` = "1min"
  val `2min` = "2min"
  val `3min` = "3min"
  val `5min` = "5min"
  val `10min` = "10min"
  val `15min` = "15min"
  val `30min` = "30min"
  val hour = "hour"
  val day = "day"
  val week = "week"
  val month = "month"
}

object InstrumentType {}
