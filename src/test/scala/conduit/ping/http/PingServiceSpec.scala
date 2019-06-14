package conduit.ping.http

import org.http4s.implicits._
import org.scalatest.{FlatSpec, Matchers}
import scalaz.zio.{DefaultRuntime, UIO, ZIO}
import util.HttpTools
import org.http4s.{Status, _}
import scalaz.zio.interop.catz._

class PingServiceSpec extends FlatSpec with Matchers with HttpTools {

  import PingServiceSpec._
  import PingServiceSpec.pingService._

  val app = pingService.service.orNotFound

  "ping" should "return pong" in {
    val req = request[PingTask](Method.GET, "/ping")
    runWithEnv(
      check(app.run(req), Status.Ok, Some("pong"))
    )
  }
}

object PingServiceSpec extends DefaultRuntime {

  val pingService: PingService[Any] = PingService[Any]

  val mkEnv: UIO[Any] = UIO.succeed(0)

  def runWithEnv[E, A](task: ZIO[Any, E, A]): A =
    unsafeRun[E, A](mkEnv.flatMap(env => task.provide(env)))
}
