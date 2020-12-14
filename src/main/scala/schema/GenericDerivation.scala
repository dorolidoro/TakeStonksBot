package schema

import cats.syntax.functor._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}


object GenericDerivation {
  implicit val jsonEncoder: Encoder[Schema] = Encoder.instance {
    //register
    case registerRequest@RegisterRequest(_) => registerRequest.asJson
    case registerResponse@RegisterResponse(_, _, _) => registerResponse.asJson

    //Errors
    case errorResponse@ErrorResponse(_, _, _) => errorResponse.asJson
    case raiseException@RaiseException(_) => raiseException.asJson

    //Empty
    case emptyResponse@EmptyResponse(_, _, _) => emptyResponse.asJson

    //portfolio
    case portfolioResponse@PortfolioResponse(_, _, _) => portfolioResponse.asJson
    case portfolioCurrResponse@PortfolioCurrenciesResponse(_, _, _) => portfolioCurrResponse.asJson

    //market
    case marketInstrumentListResponse@MarketInstrumentListResponse(_, _, _) => marketInstrumentListResponse.asJson
    case marketInstrumentList@MarketInstrumentList(_, _) => marketInstrumentList.asJson
    case marketInstrument@MarketInstrument(_, _, _,_, _, _,_, _, _) => marketInstrument.asJson
    case marketInstrument@MarketInstrumentListByFigiResponse(_, _, _) => marketInstrument.asJson

    //Orders
   // case marketOrderRequest@MarketOrderRequest(_, _) => marketOrderRequest.asJson
    case limitOrderRequest@LimitOrderRequest(_, _, _) => limitOrderRequest.asJson
    case orderResponse@OrderResponse(_, _, _) => orderResponse.asJson
    case getOrderResponse@GetOrdersResponse(_, _, _) => getOrderResponse.asJson

    case orderBookResponse@OrderBookResponse(_, _, _) => orderBookResponse.asJson
    case candlesResponse@CandlesResponse(_, _, _) => candlesResponse.asJson

    //sandbox
    case sandboxSetPositionBalanceRequest@SandboxSetPositionBalanceRequest(_, _) => sandboxSetPositionBalanceRequest.asJson

  }

  implicit val jsonDecoder: Decoder[Schema] =
    List[Decoder[Schema]](
      Decoder[RegisterRequest].widen,
      Decoder[RegisterResponse].widen,

      Decoder[ErrorResponse].widen,
      Decoder[RaiseException].widen,

      Decoder[PortfolioResponse].widen,
      Decoder[PortfolioCurrenciesResponse].widen,

      Decoder[MarketInstrumentListResponse].widen,
      Decoder[MarketInstrumentList].widen,
      Decoder[MarketInstrument].widen,
      Decoder[MarketInstrumentListByFigiResponse].widen,

      //Decoder[MarketOrderRequest].widen,
      Decoder[LimitOrderRequest].widen,
      Decoder[OrderResponse].widen,
      Decoder[GetOrdersResponse].widen,

      Decoder[OrderBookResponse].widen,
      Decoder[CandlesResponse].widen,

      Decoder[SandboxSetPositionBalanceRequest].widen

    ).reduceLeft(_ or _)
}