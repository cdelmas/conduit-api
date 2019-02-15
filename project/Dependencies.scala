import sbt._

object Dependencies {

  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % "0.10.0")

  lazy val http4s = Seq(
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-circe",
    "org.http4s" %% "http4s-blaze-server",
    "org.http4s" %% "http4s-blaze-client"
  ).map(_ % "0.20.0-SNAPSHOT")

  lazy val fs2 = "co.fs2" %% "fs2-core" % "1.0.3"

  lazy val refined = "eu.timepit" %% "refined" % "0.9.4"

  lazy val nScalaTime = "com.github.nscala-time" %% "nscala-time" % "2.22.0"

  lazy val fetch = "com.47deg" %% "fetch" % "1.0.0-RC2"

  lazy val monocle = "com.github.julien-truffaut" %% "monocle-core" % "1.5.0"

  /*lazy val zio = Seq(
    "org.scalaz" %% "scalaz-zio",
    "org.scalaz" %% "scalaz-zio-interop-cats"
  ).map(_ % "0.6.1")*/ // TODO use it eventually

  lazy val doobie = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-postgres"
  ).map(_ % "0.6.0")

  lazy val tsec = Seq(
    "io.github.jmcardon" %% "tsec-common",
    "io.github.jmcardon" %% "tsec-mac",
    "io.github.jmcardon" %% "tsec-signatures",
    "io.github.jmcardon" %% "tsec-jwt-mac",
    "io.github.jmcardon" %% "tsec-jwt-sig",
    "io.github.jmcardon" %% "tsec-http4s"
  ).map(_ % "0.0.1-M11")

  lazy val cats = "org.typelevel" %% "cats-core" % "1.6.0"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
}
