package conduit

import cats.effect._
//import fs2.Stream
//import io.circe.Json
//import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
//import org.http4s.headers._
//import org.http4s.multipart.Multipart
import org.http4s.server._
//import org.http4s.server.middleware.authentication.BasicAuth
//import org.http4s.server.middleware.authentication.BasicAuth.BasicAuthenticator
import org.http4s._
//import scala.concurrent.duration._

class ConduitApi[F[_]](implicit F: Effect[F]) extends Http4sDsl[F] {
  def routes /*( implicit timer: Timer[F])*/: HttpRoutes[F] =
    Router[F](
      "" -> rootRoutes
      //"/auth" -> authRoutes
    )
  def rootRoutes /*( implicit timer: Timer[F] )*/: HttpRoutes[F] =
    HttpRoutes.of[F] {
      /*case _ -> Root =>
        // The default route result is NotFound. Sometimes MethodNotAllowed is more appropriate.
        MethodNotAllowed(Allow(GET))
       */
      case GET -> Root / "ping" =>
        // EntityEncoder allows for easy conversion of types to a response body
        Ok("pong")
      /*
      case GET -> Root / "streaming" =>
        // It's also easy to stream responses to clients
        Ok(dataStream(100))

      case req @ GET -> Root / "ip" =>
        // It's possible to define an EntityEncoder anywhere so you're not limited to built in types
        val json = Json.obj("origin" -> Json.fromString(req.remoteAddr.getOrElse("unknown")))
        Ok(json)

      case GET -> Root / "content-change" =>
        // EntityEncoder typically deals with appropriate headers, but they can be overridden
        Ok("<h2>This will have an html content type!</h2>", `Content-Type`(MediaType.text.html))

      ///////////////////////////////////////////////////////////////
      //////////////// Dealing with the message body ////////////////
      case req @ POST -> Root / "echo" =>
        // The body can be used in the response
        Ok(req.body).map(_.putHeaders(`Content-Type`(MediaType.text.plain)))

      case req @ POST -> Root / "echo2" =>
        // Even more useful, the body can be transformed in the response
        Ok(req.body.drop(6), `Content-Type`(MediaType.text.plain))


      ///////////////////////////////////////////////////////////////
      ////////////////////// Blaze examples /////////////////////////

      // You can use the same service for GET and HEAD. For HEAD request,
      // only the Content-Length is sent (if static content)
      case GET -> Root / "helloworld" =>
        helloWorldService
      case HEAD -> Root / "helloworld" =>
        helloWorldService

      // HEAD responses with Content-Length, but empty content
      case HEAD -> Root / "head" =>
        Ok("", `Content-Length`.unsafeFromLong(1024))

      // Response with invalid Content-Length header generates
      // an error (underflow causes the connection to be closed)
      case GET -> Root / "underflow" =>
        Ok("foo", `Content-Length`.unsafeFromLong(4))

      // Response with invalid Content-Length header generates
      // an error (overflow causes the extra bytes to be ignored)
      case GET -> Root / "overflow" =>
        Ok("foo", `Content-Length`.unsafeFromLong(2))

      ///////////////////////////////////////////////////////////////
      //////////////// Form encoding example ////////////////////////

      case req @ POST -> Root / "form-encoded" =>
        // EntityDecoders return an F[A] which is easy to sequence
        req.decode[UrlForm] { m =>
          val s = m.values.mkString("\n")
          Ok(s"Form Encoded Data\n$s")
        }

      ///////////////////////////////////////////////////////////////
      //////////////////////// Multi Part //////////////////////////

      case req @ POST -> Root / "multipart" =>
        req.decode[Multipart[F]] { m =>
          Ok(s"""Multipart Data\nParts:${m.parts.length}\n${m.parts.map(_.name).mkString("\n")}""")
        }*/
    }
  /*
  def helloWorldService: F[Response[F]] = Ok("Hello World!")

  // This is a mock data source, but could be a Process representing results from a database
  def dataStream(n: Int)(implicit timer: Timer[F]): Stream[F, String] = {
    val interval = 100.millis
    val stream = Stream
      .awakeEvery[F](interval)
      .evalMap(_ => timer.clock.realTime(MILLISECONDS))
      .map(time => s"Current system time: $time ms\n")
      .take(n.toLong)

    Stream.emit(s"Starting $interval stream intervals, taking $n results\n\n") ++ stream
  }

  // Services can be protected using HTTP authentication.
  val realm = "testrealm"

  val authStore: BasicAuthenticator[F, String] = (creds: BasicCredentials) =>
    if (creds.username == "username" && creds.password == "password") F.pure(Some(creds.username))
    else F.pure(None)

  // An AuthedService[A, F] is a Service[F, (A, Request[F]), Response[F]] for some
  // user type A.  `BasicAuth` is an auth middleware, which binds an
  // AuthedService to an authentication store.
  val basicAuth: AuthMiddleware[F, String] = BasicAuth(realm, authStore)

  def authRoutes: HttpRoutes[F] =
    basicAuth(AuthedService[String, F] {
      // AuthedServices look like Services, but the user is extracted with `as`.
      case GET -> Root / "protected" as user =>
        Ok(s"This page is protected using HTTP authentication; logged in as $user")
    })*/
}

object ConduitApi {

  def apply[F[_]: Effect /*: ContextShift*/ ]: ConduitApi[F] =
    new ConduitApi[F]

}
