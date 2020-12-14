package schema

import schema.CandleResolution.CandleResolution

case class CandlesResponse(trackingId: String,
                           status: String,
                           payload: Candles) extends Schema

case class Candles(figi: String,
                   interval: CandleResolution,
                   candles: Seq[Candle])

case class Candle(figi: String,
                  interval: CandleResolution,
                  o: Double, // Цена открытия
                  c: Double, // Цена закрытия
                  h: Double, // Наибольшая цена
                  l: Double, // Наименьшая цена
                  v: Int,   // Объем торгов
                  time: String)
