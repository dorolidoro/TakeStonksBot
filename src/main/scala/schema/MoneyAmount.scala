package schema

import schema.Currency.Currency

case class MoneyAmount (
                         currency: Currency,
                         value: Double
                       )
