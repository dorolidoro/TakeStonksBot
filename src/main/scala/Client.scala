//import akka.http.scaladsl.model.{ContentTypes, HttpEntity, Uri, headers}
//import akka.http.scaladsl.model.headers.OAuth2BearerToken
//import akka.http.scaladsl.model.HttpMethods._
//import io.circe.generic.auto._
//import io.circe.syntax._
//import schema.GenericDerivation._
//import akka.actor.ActorSystem
//import akka.stream.ActorMaterializer
//import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
//import entrypoint.SLTPHTTPApp._
//import schema.{PortfolioResponse, RegisterRequest, RegisterResponse}
//import service.ApiClient
//
//import scala.util.{Failure, Success}
//import scala.concurrent.{Await, ExecutionContext, Future}
//
//case class Client(host: Uri, token: String, brokerAccountId: String, isAuth: Boolean) {
//
//  //case class Configuration (host: Uri, auth: OAuth2BearerToken, client: Client)
//}
//
//class TFApi(client: Client) {
//  val api = new ApiClient(client.token)
//  val authClient: Future[Client] = register(client)
//  val portfolio = new Portfolio(api, authClient)
//
//  def register(client: Client): Future[Client] = {
//    api.callApi[RegisterResponse](method = POST, resource = "/sandbox/register",
//      entity = HttpEntity(ContentTypes.`application/json`, RegisterRequest("Tinkoff").asJson.noSpaces))
//      .map {
//        case res@RegisterResponse(_,_,_) => client.copy(brokerAccountId = res.payload.brokerAccountId, isAuth = true)
//        case _ => client
//      }
//  }
//}
//
//class Portfolio(api: ApiClient, client: Future[Client]) {
//   def getPortfolio() ={
//    client.flatMap ( cl =>
//        api.callApi[PortfolioResponse](method = GET, resource = "/portfolio", params = Map("brokerAccountId" -> cl.brokerAccountId))
//    )
//  }
//}
