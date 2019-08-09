package conduit.generic.model

sealed trait Error

case object Unauthorized extends Error
case class GenericErrorModel(errors: Errors) extends Error
case class Errors(body: Array[String])
