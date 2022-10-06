val circeVersion = "0.14.2"

val scalaTestArtifact = "org.scalatest" %% "scalatest" % "3.2.+" % Test
val telepathyDep = "com.salesforce.mce" %% "telepathy" % "1.5.0"
val typesafeConfigDep = "com.typesafe" % "config" % "1.4.2"
val scoptDep = "com.github.scopt" %% "scopt" % "4.1.0"

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
  assembly / test := {}  // skip test during assembly
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
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
