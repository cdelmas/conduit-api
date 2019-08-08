package conduit.users
import scalaz.zio._

package object domain extends Users.Service[Users] {

  override def byCredentials(
      email: String,
      password: String
  ): ZIO[Users, Nothing, Option[User]] =
    ZIO.accessM(_.users.byCredentials(email, password))
}
