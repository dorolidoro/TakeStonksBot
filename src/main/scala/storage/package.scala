import slick.dbio.{DBIO, DBIOAction, Effect, NoStream}

package object storage {
  type DIO[+R, -E <: Effect] = DBIOAction[R, NoStream, E]
  val DIO = DBIO
}
