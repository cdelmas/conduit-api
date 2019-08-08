package conduit.users.http

import conduit.users.domain._
import io.circe.{Decoder, Encoder}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import scalaz.zio.TaskR
import scalaz.zio.interop.catz._
import io.circe.generic.auto._

final case class UserService[R <: Users]() {
  import UserService._

  type UserTask[A] = TaskR[R, A]

  implicit def circeJsonDecoder[A](
      implicit decoder: Decoder[A]
  ): EntityDecoder[UserTask, A] = jsonOf[UserTask, A]
  implicit def circeJsonEncoder[A](
      implicit encoder: Encoder[A]
  ): EntityEncoder[UserTask, A] = jsonEncoderOf[UserTask, A]

  val dsl: Http4sDsl[UserTask] = Http4sDsl[UserTask]
  import dsl._

  def service: HttpRoutes[UserTask] = {
    HttpRoutes.of[UserTask] {
      case req @ POST -> Root / "login" =>
        req.decode[LoginUserRequest] { loginUserRequest =>
          for {
            user <- byCredentials(
              loginUserRequest.user.email,
              loginUserRequest.user.password
            )
            response <- user.fold(Unauthorized())(u => Ok(UserResponse(u)))
          } yield response
        }
    }
  }
}

object UserService {
  final case class LoginUserRequest(user: LoginUser)
  final case class LoginUser(
      email: String,
      password: String
  )
  final case class UserResponse(user: User)

  def apply[R <: Users]: UserService[R] = new UserService[R]
}
