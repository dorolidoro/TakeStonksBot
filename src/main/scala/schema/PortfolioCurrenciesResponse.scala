package schema

import schema.Currency.Currency

case class PortfolioCurrenciesResponse(
                                        trackingId: String,
                                        status: String,
                                        payload: Currencies
                                      ) extends Schema

case class Currencies(currencies: Seq[CurrencyPosition])

case class CurrencyPosition(
                             currency: Currency,
                             balance: Double,
                             blocked: Option[Double]
                           )




