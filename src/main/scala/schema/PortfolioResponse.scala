package schema

case class Portfolio (
                       positions: Seq[PortfolioPosition]
                     )

case class PortfolioPosition (
                               figi: String,
                               ticker: String,
                               isin: Option[String],
                               instrumentType: String,
                               balance: Double,
                               blocked: Option[Double],
                               expectedYield: Option[MoneyAmount],
                               lots: Int,
                               averagePositionPrice: Option[MoneyAmount],
                               averagePositionPriceNoNkd: Option[MoneyAmount],
                               name: String
                             )

case class PortfolioResponse (
                               trackingId: String,
                               status: String,
                               payload: Portfolio
                             ) extends Schema
