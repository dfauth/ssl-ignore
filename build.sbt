lazy val root = (project in file("."))
  .settings(
    name := "sbt-ssl-ignore",
    organization := "com.github.dfauth.sbt",
    version := "0.1-SNAPSHOT",
    sbtPlugin := true,
  )
