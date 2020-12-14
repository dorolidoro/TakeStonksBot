package schema.websocket

import cats.syntax.functor._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}


trait WSSchema

object GenericDerivation {
  implicit val jsonEncoder: Encoder[WSSchema] = Encoder.instance {
    case candleRequest@CandleRequest(_, _, _, _) => candleRequest.asJson
    case instrumentInfoRequest@InstrumentInfoRequest(_, _, _) => instrumentInfoRequest.asJson
    case orderBookRequest@OrderBookRequest(_, _, _, _) => orderBookRequest.asJson

    case candleResponse@CandleResponse(_, _, _) => candleResponse.asJson
    case instrumentInfoResponse@InstrumentInfoResponse(_, _, _) => instrumentInfoResponse.asJson
    case orderBookResponse@OrderBookResponse(_, _, _) => orderBookResponse.asJson
    case errorWSResponse@ErrorWSResponse(_, _, _) => errorWSResponse.asJson
  }

  implicit val jsonDecoder: Decoder[WSSchema] =
    List[Decoder[WSSchema]](
      Decoder[CandleRequest].widen,
      Decoder[InstrumentInfoRequest].widen,
      Decoder[OrderBookRequest].widen,

      Decoder[CandleResponse].widen,
      Decoder[InstrumentInfoResponse].widen,
      Decoder[OrderBookResponse].widen,
      Decoder[ErrorWSResponse].widen,
    ).reduceLeft(_ or _)

}
