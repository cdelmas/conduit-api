package conduit.users.domain

import conduit.common.error._
import scalaz.zio.{Ref, ZIO}

trait Users extends Serializable {

  val users: Users.Service[Any]
}

object Users extends Serializable {

  trait Service[R] extends Serializable {

    def byCredentials(
        email: String,
        password: String
    ): ZIO[R, ConduitError, User]
  }

  final case class InMemoryUsers(ref: Ref[Map[UserId, User]])
      extends Service[Any] {
    override def byCredentials(
        email: String,
        password: String
    ): ZIO[Any, ConduitError, User] =
      (for {
        map <- ref.get
        user <- ZIO.fromOption(map.values.find(u => u.email == email))
      } yield user) mapError { _ =>
        NoSuchUser(email)
      }
  }
}
