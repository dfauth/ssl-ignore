package com.github.dfauth.sslIgnore

import sbt._
import sbt.Keys._
import complete.DefaultParsers._
import com.github.dfauth.sslIgnore.SslUtil._
import com.typesafe.scalalogging.LazyLogging

object SslIgnorePlugin extends AutoPlugin with LazyLogging {

  object autoImport {
    val trustStore = inputKey[Unit]("set the trust store")
  }

  override lazy val projectSettings = Seq(
    autoImport.trustStore := {
      val args:Seq[String] = spaceDelimited("").parsed
      useTrustStore(args(0), null, getApproveAllTrustManagers)
      System.out.println(s"plugin ${this} loaded")
    }
  )

}
