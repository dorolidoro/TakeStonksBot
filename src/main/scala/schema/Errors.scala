package schema

case class PayloadError(message: String, code: String) extends Schema

case class ErrorResponse(trackingId: String, status: String, payload: PayloadError) extends Schema

case class RaiseException(message: String) extends Schema
