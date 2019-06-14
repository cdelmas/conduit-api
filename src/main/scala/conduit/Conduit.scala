package conduit

import cats.effect._
import conduit.ping.http.PingService
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import org.http4s.server.middleware.CORS
import scalaz.zio._
import scalaz.zio.blocking.Blocking
import scalaz.zio.clock.Clock
import scalaz.zio.console._
import scalaz.zio.system._
import scalaz.zio.interop.catz._
import scalaz.zio.scheduler.Scheduler

object Conduit extends App {

  type AppEnvironment = Clock with Console with Blocking with System // with XXXRepository with ....
  type AppTask[A] = TaskR[AppEnvironment, A]

  final case class Config(host: String)

  override def run(args: List[String]): ZIO[Conduit.Environment, Nothing, Int] =
    (for {
      config <- loadConfig
      httpApp = Router[AppTask](
        "/api" -> PingService[AppEnvironment].service
      ).orNotFound
      server = ZIO.runtime[AppEnvironment].flatMap { implicit rs =>
        BlazeServerBuilder[AppTask]
          .bindHttp(8080, config.host)
          .withHttpApp(CORS(httpApp))
          .serve
          .compile[AppTask, AppTask, ExitCode]
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
