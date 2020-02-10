package conduit.common

object error {
  sealed trait ConduitError extends Exception
  case class NoSuchUser(user: String) extends ConduitError
  case class UnexpectedError(throwable: Throwable) extends ConduitError
}
