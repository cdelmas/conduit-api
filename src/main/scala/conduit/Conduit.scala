package conduit

import scalaz.zio.interop.catz._
import cats.effect._
import conduit.ping.http.PingRoutes
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import org.http4s.server.middleware.CORS
import scalaz.zio._
import scalaz.zio.blocking.Blocking
import scalaz.zio.clock.Clock
import scalaz.zio.console._
import scalaz.zio.system._
import scalaz.zio.scheduler.Scheduler
import tapir.docs.openapi._
import tapir.openapi.circe.yaml._
import tapir.swagger.http4s.SwaggerHttp4s

object Conduit extends App {

  type AppEnvironment = Clock with Console with Blocking with System // with XXXRepository with ....

  final case class Config(host: String)

  private val pingRoutes = new PingRoutes[AppEnvironment]
  private val yaml = pingRoutes.endpoints.toOpenAPI("Ping", "1.0").toYaml

  override def run(args: List[String]): ZIO[Conduit.Environment, Nothing, Int] =
    (for {
      config <- loadConfig
      httpApp = Router(
        "/api" -> pingRoutes.routes,
        "/docs" -> new SwaggerHttp4s(yaml).routes[TaskR[AppEnvironment, ?]]
      ).orNotFound
      server = ZIO.runtime[AppEnvironment].flatMap { implicit rs =>
        BlazeServerBuilder[ZIO[AppEnvironment, Throwable, ?]]
          .bindHttp(8080, config.host)
          .withHttpApp(CORS(httpApp))
          .serve
          .compile[ZIO[AppEnvironment, Throwable, ?], ZIO[AppEnvironment, Throwable, ?], ExitCode]
          .drain
      }
      program <- server.provideSome[Environment] { base =>
        new Clock with Console with Blocking with System {
          override val clock: Clock.Service[Any] = base.clock
          override val blocking: Blocking.Service[Any] = base.blocking
          override val console: Console.Service[Any] = base.console
          override val scheduler: Scheduler.Service[Any] = base.scheduler
          override val system: System.Service[Any] = base.system
        }
      }
    } yield program).foldM(
      err => putStrLn(s"Failure: $err") *> ZIO.succeed(1),
      _ => ZIO.succeed(0)
    )

  private def loadConfig =
    for {
      host <- system.env("APP_HOST") // do the necessary stuff ~> get vars and build a config object
    } yield Config(host getOrElse "0.0.0.0")
}
