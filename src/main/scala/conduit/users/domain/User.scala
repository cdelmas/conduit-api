package conduit.users.domain

case class UserId(uuid: String) extends AnyVal

case class User(
    email: String,
    token: String,
    username: String,
    bio: String,
    image: String
)
