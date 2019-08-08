import sbt._

object Dependencies {

  lazy val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % "0.10.0")

  lazy val http4s = Seq(
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-circe",
    "org.http4s" %% "http4s-blaze-server",
    "org.http4s" %% "http4s-blaze-client"
  ).map(_ % "0.20.0")

  lazy val fs2 = "co.fs2" %% "fs2-core" % "1.0.3"

  lazy val refined = "eu.timepit" %% "refined" % "0.9.4"

  lazy val fetch = "com.47deg" %% "fetch" % "1.0.0-RC2"

  lazy val monocle = "com.github.julien-truffaut" %% "monocle-core" % "1.5.0"

  lazy val tapir = Seq(
    "com.softwaremill.tapir" %% "tapir-core",
    "com.softwaremill.tapir" %% "tapir-http4s-server",
    "com.softwaremill.tapir" %% "tapir-swagger-ui-http4s",
    "com.softwaremill.tapir" %% "tapir-openapi-docs",
    "com.softwaremill.tapir" %% "tapir-openapi-circe-yaml",
    "com.softwaremill.tapir" %% "tapir-json-circe"
  ).map(_ % "0.9.1")

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

  lazy val zio = Seq(
    "org.scalaz" %% "scalaz-zio",
    "org.scalaz" %% "scalaz-zio-interop-cats"
  ) map (_ % "1.0-RC4")

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
}
