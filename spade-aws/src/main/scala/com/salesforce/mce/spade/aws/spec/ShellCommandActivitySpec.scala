package com.salesforce.mce.spade.aws.spec

import io.circe._
import io.circe.generic.semiauto._

case class ShellCommandActivitySpec(
  lines: Seq[String],
  outputUri: Option[String],
  executionTimeout: Option[Int],
  deliveryTimeout: Option[Int]
)

object ShellCommandActivitySpec {

  implicit val decoder: Decoder[ShellCommandActivitySpec] = deriveDecoder[ShellCommandActivitySpec]
  implicit val encoder: Encoder[ShellCommandActivitySpec] = deriveEncoder[ShellCommandActivitySpec]

}
