package util
import cats.effect.IO
import org.http4s.{EntityDecoder, Response, Status}
import org.scalatest.Assertion
import org.scalatest.Matchers._


object TestUtils {
  def check[A](actual: IO[Response[IO]],
               expectedStatus: Status,
               expectedBody: Option[A])(
                implicit ev: EntityDecoder[IO, A]
              ): Assertion = {
    val actualResp = actual.unsafeRunSync
    actualResp.status should be(expectedStatus)
    expectedBody match {
      case None           => actualResp.body.compile.toVector.unsafeRunSync shouldBe empty
      case Some(expected) => actualResp.as[A].unsafeRunSync should be(expected)
    }
  }
}