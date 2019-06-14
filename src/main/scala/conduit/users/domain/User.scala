package conduit.users.domain

case class UserId(uuid: xxx) extends AnyVal

case class User(

                 email: String,
                token: String,
                username: String,
                bio: String,
                image: String)
