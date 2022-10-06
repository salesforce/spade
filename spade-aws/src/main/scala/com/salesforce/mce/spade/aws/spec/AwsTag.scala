package com.salesforce.mce.spade.aws.spec

import io.circe._
import io.circe.generic.semiauto._

case class AwsTag(
  key: String,
  value: String
)

object AwsTag {

  implicit val decoder: Decoder[AwsTag] = deriveDecoder[AwsTag]
  implicit val encoder: Encoder[AwsTag] = deriveEncoder[AwsTag]

}
