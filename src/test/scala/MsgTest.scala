import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers
import service.MsgCreator



class MsgTest extends AsyncFunSuite with Matchers {

  test("Portfolio") {
    for {
      msg <- MsgCreator.message(Samples.portfolioRes)
    } yield(assert(msg == Samples.portfolioExpectMsg))
  }

  test("PortfolioCurr") {
    for {
      msg <- MsgCreator.message(Samples.portfolioCurRes)
    } yield(assert(msg == Samples.portfolioCurExpectMsg))
  }

  test("Placed order") {
    for {
      msg <- MsgCreator.message(Samples.orderRes)
    } yield(assert(msg == Samples.orderExpectMsg))
  }

  test("Get orders") {
    for {
      msg <- MsgCreator.message(Samples.getOrdersRes)
    } yield(assert(msg == Samples.getOrdersExpectMsg))
  }

  test("SearchByTicket") {
    for {
      msg <- MsgCreator.message(Samples.searchByTicketRes)
    } yield(assert(msg == Samples.searchByTicketsExpectMsg))
  }

  test("OrderBook") {
    for {
      msg <- MsgCreator.message(Samples.orderBookRes)
    } yield(assert(msg == Samples.orderBookExpectMsg))
  }

  test("Error") {
    for {
      msg <- MsgCreator.message(Samples.errorRes)
    } yield(assert(msg == Samples.errorExpectMsg))
  }

  }

