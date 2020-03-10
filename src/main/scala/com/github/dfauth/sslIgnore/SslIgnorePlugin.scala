package com.github.dfauth.sslIgnore

import sbt._
import com.github.dfauth.sslIgnore.SslUtil._

object SslIgnorePlugin extends AutoPlugin {

  useTrustStore("TLSv1.2", getEmptyKeyManagers(), getApproveAllTrustManagers)
}
