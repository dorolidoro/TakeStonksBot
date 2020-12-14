package schema

case class EmptyPayload()
case class EmptyResponse(trackingId: String, status: String, payload: EmptyPayload) extends Schema



