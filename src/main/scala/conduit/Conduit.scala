package conduit

import cats.effect._
import conduit.ping.http.PingRoutes
import conduit.users.database.DoobieUsers
import conduit.users.domain.Users
import conduit.users.http.UserRoutes
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import org.http4s.server.middleware.{CORS, Logger}
import scalaz.zio._
import scalaz.zio.blocking.Blocking
import scalaz.zio.clock.Clock
import scalaz.zio.console._
import scalaz.zio.interop.catz._
import scalaz.zio.scheduler.Scheduler
import scalaz.zio.system._
import tapir.docs.openapi._
import tapir.openapi.circe.yaml._
import tapir.swagger.http4s.SwaggerHttp4s

object Conduit extends App {

  type AppEnvironment = Clock with Console with Blocking with System with Users

  final case class Config(host: String)

  private val pingRoutes = new PingRoutes[AppEnvironment]
  private val userRoutes = new UserRoutes[AppEnvironment]
  private val allEndpoints = pingRoutes.endpoints ++ userRoutes.endpoints
  private val yaml = allEndpoints.toOpenAPI("Conduit", "0.1").toYaml

  override def run(args: List[String]): ZIO[Conduit.Environment, Nothing, Int] =
    (for {
      config <- loadConfig
      httpApp = Router(
        "/" -> pingRoutes.routes,
        "/" -> userRoutes.routes,
        "/docs" -> new SwaggerHttp4s(yaml).routes[TaskR[AppEnvironment, ?]]
      ).orNotFound
      theApp = Logger.httpApp[ZIO[AppEnvironment, Throwable, ?]](logHeaders = true, logBody = true)(httpApp)
      server = ZIO.runtime[AppEnvironment].flatMap { implicit rs =>
        BlazeServerBuilder[ZIO[AppEnvironment, Throwable, ?]]
          .bindHttp(8080, config.host)
          .withHttpApp(CORS(theApp))
          .serve
          .compile[ZIO[AppEnvironment, Throwable, ?], ZIO[AppEnvironment, Throwable, ?], ExitCode]
          .drain
      }
      program <- server.provideSome[Environment] { base =>
        new Clock with Console with Blocking with System with DoobieUsers {
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
