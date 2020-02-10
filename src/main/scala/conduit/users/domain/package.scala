package conduit.users
import conduit.common.error.ConduitError
import scalaz.zio._

package object domain extends Users.Service[Users] {

  override def byCredentials(
      email: String,
      password: String
  ): ZIO[Users, ConduitError, User] =
    ZIO.accessM(_.users.byCredentials(email, password))
}
