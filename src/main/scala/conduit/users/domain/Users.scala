package conduit.users.domain

import scalaz.zio.{Ref, ZIO}

trait Users extends Serializable {

  val users: Users.Service[Any]
}

object Users extends Serializable {

  trait Service[R] extends Serializable {

    def byCredentials(email: String, password: String): ZIO[R, Nothing, Option[User]]
  }

  final case class InMemoryUsers(ref: Ref[Map[UserId, User]]) extends Service[Any] {
    override def byCredentials(email: String, password: String): ZIO[Any, Nothing, Option[User]] =
      for {
        map <- ref.get
        x = map.values.find(u => u.email == email)
      } yield x
  }
}
