package conduit.ping.http

import conduit.generic.http._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import scalaz.zio.interop.catz._
import scalaz.zio.{TaskR, ZIO}
import tapir._

import tapir.server.http4s._

class PingRoutes[R] extends Http4sDsl[TaskR[R, ?]]() {

  private val pingEndpoint = baseEndpoint.get.in("ping").out(stringBody)

  def routes: HttpRoutes[TaskR[R, ?]] = {
    pingEndpoint.toRoutes { _ =>
      pong
    }
  }

  private def pong: ZIO[R, Throwable, Either[Unit, String]] = ZIO.succeed(Right("pong"))

  val endpoints = List(pingEndpoint)
}
