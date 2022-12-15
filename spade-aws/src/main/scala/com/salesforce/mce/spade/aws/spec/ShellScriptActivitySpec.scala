package com.salesforce.mce.spade.aws.spec

import io.circe._
import io.circe.generic.semiauto._

case class ShellScriptActivitySpec(
  scriptLocation: String,
  args: Seq[String],
  outputUri: Option[String],
  executionTimeout: Option[Int],
  deliveryTimeout: Option[Int]
)

object ShellScriptActivitySpec {

  implicit val decoder: Decoder[ShellScriptActivitySpec] = deriveDecoder[ShellScriptActivitySpec]
  implicit val encoder: Encoder[ShellScriptActivitySpec] = deriveEncoder[ShellScriptActivitySpec]

}
