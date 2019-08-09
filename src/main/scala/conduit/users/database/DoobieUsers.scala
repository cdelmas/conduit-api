package conduit.users.database

import conduit.users.domain.{User, Users}
import doobie.util.query.Query0
//import doobie.util.transactor.Transactor
import scalaz.zio.{/*Task, */ZIO}
//import scalaz.zio.interop.catz._
import doobie.implicits._

trait DoobieUsers extends Users {
  //import DoobieUsers._

  //protected def xa: Transactor[Task] TODO: really go to the database

  override val users: Users.Service[Any] = new Users.Service[Any] {
    override def byCredentials(
        email: String,
        password: String
    ): ZIO[Any, Nothing, Option[User]] =
      if (email contains 'z') ZIO.succeed(None) else ZIO.succeed(Some(User("mail@me.org", "tok", "me", "bio", "image")))
      /*SQL // TODO: really go to the database
        .byCredentials(email, password)
        .option
        .transact(xa)
        .orDie*/
  }
}

object DoobieUsers {
  object SQL {
    def byCredentials(email: String, password: String): Query0[User] = sql"""
      SELECT * from Users WHERE email = $email AND password = $password
    """.query[User]
  }
}
