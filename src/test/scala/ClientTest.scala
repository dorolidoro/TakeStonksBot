//
//import org.scalatest.BeforeAndAfterAll
//import org.scalatest.funsuite.AsyncFunSuite
//
//
//class ClientTest extends AsyncFunSuite with BeforeAndAfterAll {
//
//  test("get Portfolio") {
//    val tf = new TFApi(Client(host = "https://api-invest.tinkoff.ru/openapi/sandbox",
//      token = "token",
//      brokerAccountId = "", isAuth = false))
//    for {
//      portfolio <- tf.portfolio.getPortfolio()
//
//    } yield {
//      assert("Ok" == portfolio.status)
//
//    }
//  }
//}
