package schema.websocket

case class OrderBookRequest(event: String,
                            figi: String,
                            depth: Int,
                            request_id: Option[String] = None)
  extends WSSchema

