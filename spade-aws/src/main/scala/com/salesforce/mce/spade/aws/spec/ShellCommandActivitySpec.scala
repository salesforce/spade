package com.salesforce.mce.spade.aws.spec

import io.circe._
import io.circe.generic.semiauto._

case class ShellCommandActivitySpec(
  lines: Seq[String],
  outputS3BucketName: String,
  outputS3KeyPrefix: String,
  executionTimeout: Int,
  deliveryTimeout: Int
)

object ShellCommandActivitySpec {

  implicit val decoder: Decoder[ShellCommandActivitySpec] = deriveDecoder[ShellCommandActivitySpec]
  implicit val encoder: Encoder[ShellCommandActivitySpec] = deriveEncoder[ShellCommandActivitySpec]

}
