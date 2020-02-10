package conduit.users.database

import conduit.common.error.{ConduitError, NoSuchUser, UnexpectedError}
import conduit.users.domain.{User, Users}
import doobie.implicits._
import doobie.util.query.Query0
import doobie.util.transactor.Transactor
import scalaz.zio.interop.catz._
import scalaz.zio.{Task, ZIO}

trait DoobieUsers extends Users {

  import DoobieUsers._

  protected def xa: Transactor[Task]

  override val users: Users.Service[Any] = new Users.Service[Any] {
    override def byCredentials(
        email: String,
        password: String
    ): ZIO[Any, ConduitError, User] =
      SQL
        .byCredentials(email, password)
        .option
        .transact(xa)
        .mapError(UnexpectedError)
        .flatMap {
          case Some(u) => ZIO.succeed(u)
          case None    => ZIO.fail(NoSuchUser(email))
        }
  }
}

object DoobieUsers {
  object SQL {
    def byCredentials(email: String, password: String): Query0[User] = sql"""
      SELECT * from Users WHERE email = $email AND password = $password
    """.query[User]
  }
}
