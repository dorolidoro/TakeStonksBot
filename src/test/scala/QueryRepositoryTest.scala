import org.scalatest.matchers.should.Matchers
import storage.{DataBaseQuery, SLTPOrders, Users}


class QueryRepositoryTest extends DatabaseSuite with Matchers {
  val rep = new DataBaseQuery

  test("findUser") {
    for {
      user <- rep.findUser("1")
    } yield assert(user.contains(Users("1", "token", "tf_id_1")))
  }

  test("addUser") {
    //random gen!
    val newUser = Users("2", "token2", "broker2")
    for {
      _ <- rep.addUser(newUser)
      user <- rep.findUser("2")
    } yield assert(user.contains(newUser))
  }

  test("findSLTPOrders") {
    for {
      order <- rep.findSLTPOrders("213")
    } yield assert(order.contains(SampleOrders.head))
  }

  test("addSLTPOrders") {
    //random gen!
    val newOrder = SLTPOrders("2", "TP", 120, "New", "Order complete")
    for {
      _ <- rep.addSLTPOrders(newOrder)
      order <- rep.findSLTPOrders("333")
    } yield assert(order.contains(newOrder))
  }

  test("findOrdersByTypeAndStatus") {
    for {
     orders <- rep.findOrdersByTypeAndStatus("SL", "New")
    } yield assert(orders.contains(SampleOrders.headOption.getOrElse(false)))
  }

}
