package schema.websocket

case class InstrumentInfoRequest(event: String, figi: String, request_id: Option[String] = None)
  extends WSSchema

