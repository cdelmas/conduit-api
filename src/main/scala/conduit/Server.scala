package conduit

import cats.effect._
import cats.implicits._
import fs2._
import org.http4s.HttpApp
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._

object Conduit extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    ConduitApp.stream[IO].compile.drain.as(ExitCode.Success)
}

object ConduitApp {

  def httpApp[F[_]: Effect: ContextShift: Timer]: HttpApp[F] =
    Router(
      "/api" -> ConduitApi[F].routes
    ).orNotFound

  def stream[F[_]: ConcurrentEffect: Timer: ContextShift]: Stream[F, ExitCode] =
    BlazeServerBuilder[F]
      .bindHttp(8080)
      .withHttpApp(httpApp[F])
      .serve

}
