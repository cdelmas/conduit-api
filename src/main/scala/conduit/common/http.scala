package conduit.common

object http {
  case class GenericErrorModel(errors: ErrorList)
  case class ErrorList(body: List[String])

  sealed trait ErrorResponse
  case class UnauthorizedErrorReponse(error: GenericErrorModel) extends ErrorResponse
  case class InternalErrorResponse(error: GenericErrorModel) extends ErrorResponse
}
