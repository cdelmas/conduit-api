import Dependencies._

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "io.github.cdelmas"
ThisBuild / organizationName := "cdelmas"

lazy val root = (project in file("."))
  .settings(
    name := "conduit-api",
    libraryDependencies ++= Seq(
      fs2,
      refined,
      fetch,
      monocle,
      scalaTest % Test
    ) ++ zio ++ circe ++ http4s ++ doobie ++ tsec ++ tapir,
    resolvers += Resolver.sonatypeRepo("releases"),
    assemblyJarName in assembly := "conduit-api.jar",
    mainClass in assembly := Some("conduit.Conduit"),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0-M4")
  )
