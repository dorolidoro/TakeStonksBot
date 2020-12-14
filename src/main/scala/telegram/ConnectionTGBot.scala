package telegram


import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest}


import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object ConnectionTGBot{
  import TakeStonksBot.{as, materializer, ec}

  def setUrl(): Future[String] ={
    import com.typesafe.config.ConfigFactory

    val url = ConfigFactory.load("application.conf").getString("localhost.url")
    val token = ConfigFactory.load("application.conf").getString("localhost.token")

    val response = Http().singleRequest(HttpRequest(POST,
      uri = s"https://api.telegram.org/bot${token}/setWebhook",
      entity = HttpEntity(ContentTypes.`application/json`, s"""{
                                                             |    "url": "${url}"
                                                             |}""".stripMargin)
    ))

    response.flatMap(response => response.entity.toStrict(2.seconds)).
      map(entity => entity.data.utf8String)
  }

}
