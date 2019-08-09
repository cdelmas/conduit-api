package conduit.users.http


import conduit.generic.http._
import conduit.generic.model._
import conduit.users.domain._
import io.circe.generic.auto._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import scalaz.zio.interop.catz._
import scalaz.zio.{TaskR, ZIO}
import tapir._
import tapir.json.circe._
import tapir.model.StatusCodes
import tapir.server.http4s._

class UserRoutes[R <: Users] extends Http4sDsl[TaskR[R, ?]] {
  import UserRoutes._

  private val loginEndpoint = baseEndpoint.post
    .in("users")
    .in(jsonBody[LoginUserRequest])
    .out(jsonBody[UserResponse])
    .errorOut(
    oneOf[Error](
      statusMapping(StatusCodes.Unauthorized, emptyOutput.map(_ => conduit.generic.model.Unauthorized)(_ => ())),
      statusDefaultMapping(jsonBody[GenericErrorModel].description("unknown"))
    )
  )

  val routes: HttpRoutes[TaskR[R, ?]] = {
    loginEndpoint.toRoutes { loginUserRequest =>
      userLogin(loginUserRequest.user)
    }
  }

  val endpoints = List(loginEndpoint)

  private def userLogin(loginUser: LoginUser): ZIO[R, Throwable, Either[Error, UserResponse]] = {
    for {
      user <- byCredentials(loginUser.email, loginUser.password)
      response = user.fold[Either[Error, UserResponse]](Left(conduit.generic.model.Unauthorized))(u => Right(UserResponse(u)))
    } yield response
  }
}

object UserRoutes {
  final case class LoginUserRequest(user: LoginUser)
  final case class LoginUser(
      email: String,
      password: String
  )
  final case class UserResponse(user: User)
}
