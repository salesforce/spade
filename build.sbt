val circeVersion = "0.14.2"

val scalaTestArtifact = "org.scalatest" %% "scalatest" % "3.2.+" % Test
val telepathyDep = "com.salesforce.mce" %% "telepathy" % "1.5.0"
val typesafeConfigDep = "com.typesafe" % "config" % "1.4.2"
val scoptDep = "com.github.scopt" %% "scopt" % "4.1.0"

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false },
  publishTo := sonatypePublishToBundle.value,
  homepage := Some(url("https://github.com/salesforce/spade")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/salesforce/spade"),
      "scm:git:git@github.com:salesforce/spade.git"
    )
  ),
  credentials += Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    sys.env.getOrElse("SONATYPE_USERNAME",""),
    sys.env.getOrElse("SONATYPE_PASSWORD","")
  ),
  developers := List(
    Developer(
      id = "realstraw",
      name = "Kexin Xie",
      email = "kexin.xie@salesforce.com",
      url = url("http://github.com/realstraw")
    )
  ),
  useGpgPinentry := true
)

lazy val commonSettings = Seq(
  scalacOptions ++= Seq("-deprecation", "-feature", "-Xlint"), // , "-Xfatal-warnings"),
  scalaVersion := "2.13.9",
  scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 13)) => Seq("-Xlint:-byname-implicit,_")
    case _ => Seq.empty[String]
  }),
  libraryDependencies += scalaTestArtifact,
  fork := true,
  organization := "com.salesforce.mce",
  headerLicense := Some(HeaderLicense.Custom(
  """|Copyright (c) 2022, salesforce.com, inc.
     |All rights reserved.
     |SPDX-License-Identifier: BSD-3-Clause
     |For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
     |""".stripMargin
  ))
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "spade"
  )
  .aggregate(core, aws, examples)

lazy val core = (project in file("spade-core"))
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "spade-core",
    buildInfoKeys := Seq[BuildInfoKey](name, version),
    buildInfoPackage := "com.salesforce.mce.spade",
    libraryDependencies ++= Seq(
      telepathyDep,
      typesafeConfigDep,
      scoptDep
    )
  )

lazy val aws = (project in file("spade-aws"))
  .settings(commonSettings: _*)
  .settings(
    name := "spade-aws"
  )
  .dependsOn(core)

lazy val examples = (project in file("spade-examples"))
  .settings(commonSettings: _*)
  .settings(
    name := "spade-examples"
  )
  .dependsOn(aws)
