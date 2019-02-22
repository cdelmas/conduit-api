package conduit
import util.TestUtils._
import cats.effect._
import conduit.adapters.http.Ping
import org.http4s._
import org.http4s.dsl._
import org.http4s.implicits._
import org.scalatest.{FlatSpec, Matchers}

class PingSpec extends FlatSpec with Matchers with Http4sDsl[IO] {

  "ping" should "return pong" in {
    val pingHttpService = Ping.endpoints[IO].orNotFound
    val request = Request[IO](GET, Uri.unsafeFromString("/ping"))
    val response = pingHttpService.run(request)

    check(response, Ok, Some("pong"))
  }

}
