package conduit.users.http

import conduit.common.error.{ConduitError, NoSuchUser, UnexpectedError}
import conduit.common.http._
import conduit.users.domain._
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import scalaz.zio.interop.catz._
import scalaz.zio.{TaskR, ZIO}
import tapir._
import tapir.json.circe._
import tapir.model.StatusCodes
import tapir.server.http4s._

final case class LoginUserRequest(user: LoginUser)
final case class LoginUser(email: String, password: String)
final case class UserResponse(user: User)

class UserRoutes[R <: Users] extends Http4sDsl[TaskR[R, *]]() {
  type UserTask[A] = TaskR[R, A]

  implicit def circeJsonDecoder[A](
      implicit decoder: Decoder[A]
  ): EntityDecoder[UserTask, A] = jsonOf[UserTask, A]
  implicit def circeJsonEncoder[A](
      implicit encoder: Encoder[A]
  ): EntityEncoder[UserTask, A] = jsonEncoderOf[UserTask, A]

  private val loginEndpoint = endpoint.post
    .in("login")
    .in(jsonBody[LoginUserRequest])
    .out(jsonBody[UserResponse])
    .errorOut(
      oneOf[ErrorResponse](
        statusMapping(
          StatusCodes.Unauthorized,
          jsonBody[UnauthorizedErrorReponse]
        ),
        statusMapping(
          StatusCodes.InternalServerError,
          jsonBody[InternalErrorResponse]
        )
      )
    )

  val endpoints = List(loginEndpoint)

  def routes: HttpRoutes[TaskR[R, *]] = {
    loginEndpoint.toRoutes { loginUserRequest: LoginUserRequest =>
      val result = byCredentials(
        loginUserRequest.user.email,
        loginUserRequest.user.password
      )
      handleError(result) map { _ map UserResponse }
    }
  }

  /*Unauthorized(
      `WWW-Authenticate`(
        NonEmptyList.of(
          Challenge("Basic", "conduit API login"),
          Challenge("Bearer", "conduit API")
        )
      )
    )*/

  private def handleError[A](
      result: ZIO[R, ConduitError, A]
  ): ZIO[R, Throwable, Either[ErrorResponse, A]] = {
    result.foldM(
      {
        case NoSuchUser(_) =>
          ZIO.succeed(
            Left(
              UnauthorizedErrorReponse(
                GenericErrorModel(ErrorList(List("Invalid credentials")))
              )
            )
          )
        case UnexpectedError(_) =>
          ZIO.succeed(
            Left(
              InternalErrorResponse(
                GenericErrorModel(ErrorList(List("Internal error")))
              )
            )
          )
      },
      a => ZIO.succeed(Right(a))
    )
  }

}
