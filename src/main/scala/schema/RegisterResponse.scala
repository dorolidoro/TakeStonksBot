package schema

case class Account(brokerAccountType: String,
                   brokerAccountId: String)

case class RegisterResponse(trackingId: String,
                            status: String,
                            payload: Account) extends Schema
