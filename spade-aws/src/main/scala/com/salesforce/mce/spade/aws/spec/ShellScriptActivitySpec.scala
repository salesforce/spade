package com.salesforce.mce.spade.aws.spec

import io.circe._
import io.circe.generic.semiauto._

case class ShellScriptActivitySpec(
  scriptLocation: String,
  args: Seq[String],
  outputS3BucketName: String,
  outputS3KeyPrefix: String,
  executionTimeout: Int,
  deliveryTimeout: Int
)

object ShellScriptActivitySpec {

  implicit val decoder: Decoder[ShellScriptActivitySpec] = deriveDecoder[ShellScriptActivitySpec]
  implicit val encoder: Encoder[ShellScriptActivitySpec] = deriveEncoder[ShellScriptActivitySpec]

}
