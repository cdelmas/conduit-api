package conduit.ping.http

import org.http4s.implicits._
import org.scalatest.{FlatSpec, Matchers}
import scalaz.zio.{DefaultRuntime, UIO, ZIO}
import util.HttpTools
import org.http4s.{Status, _}
import scalaz.zio.interop.catz._

class PingRoutesSpec extends FlatSpec with DefaultRuntime with Matchers with HttpTools {

  type Env = Any
  private val pingRoute = new PingRoutes[Env]
  private val app = pingRoute.routes.orNotFound
  private val mkEnv: ZIO[Any, Nothing, Env] = UIO.succeed(0)

  "ping" should "return pong" in {
    val req = request[ZIO[Env, Throwable, ?]](Method.GET, "/api/ping")
    runWithEnv(
      check(app.run(req), Status.Ok, Some("pong"))
    )
  }
  
  private def runWithEnv[E, A](task: ZIO[Env, E, A]): A = {
    val toRun = for {
      env <- mkEnv
      r <- task.provide(env)
    } yield r
    unsafeRun(toRun)
  }
}
