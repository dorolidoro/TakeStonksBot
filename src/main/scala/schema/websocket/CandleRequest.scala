package schema.websocket

case class CandleRequest(event: String,
                         figi: String,
                         interval: String,
                         request_id: Option[String] = None) extends WSSchema

