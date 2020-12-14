package schema.websocket


case class ErrorWSResponse(event: String,
                         time: String, // RFC3339Nano
                         playload: ErrorWSPlayload) extends WSSchema

case class ErrorWSPlayload(error: String,
                                 request_id: Option[String])
