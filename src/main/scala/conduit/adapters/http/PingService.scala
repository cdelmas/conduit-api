package conduit.adapters.http

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import scalaz.zio.TaskR

import scalaz.zio.interop.catz._

final case class PingService[R/* <: XXX*/]() {

  type PingTask[A] = TaskR[R, A]

  val dsl: Http4sDsl[PingTask] = Http4sDsl[PingTask]
  import dsl._

  def service: HttpRoutes[PingTask] = {
    HttpRoutes.of {
      case GET -> Root / "ping" =>
        Ok("pong")
    }
  }

}

object PingService {
  def apply[R]: PingService[R] = new PingService[R]
}
