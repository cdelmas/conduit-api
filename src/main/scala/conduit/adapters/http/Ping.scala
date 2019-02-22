package conduit.adapters.http
import cats.effect.Effect
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class Ping[F[_]: Effect] extends Http4sDsl[F] {

  def ping: HttpRoutes[F] =
    HttpRoutes.of {
      case GET -> Root / "ping" =>
        Ok("pong")
    }

  def endpoints: HttpRoutes[F] = ping
}

object Ping {
  def endpoints[F[_]: Effect]: HttpRoutes[F] = new Ping[F].endpoints
}
