package schema

import schema.Currency.Currency

case class MarketInstrumentListResponse(trackingId: String,
                                        status: String,
                                        payload: MarketInstrumentList) extends Schema

case class MarketInstrumentList(total: Option[Int],
                                instruments: List[MarketInstrument]) extends Schema

case class MarketInstrumentListByFigiResponse(trackingId: String,
                                        status: String,
                                        payload: MarketInstrument) extends Schema

case class MarketInstrument(figi: String,
                            ticker: String,
                            isin: String,
                            minPriceIncrement: Option[Double], //Шаг цены
                            lot: Int,
                            minQuantity: Option[Int], /// Минимальное число инструментов для покупки должно быть не меньше, чем размер лота х количество лотов
                            currency: Option[Currency],
                            name: String,
                            `type`: String) extends Schema
