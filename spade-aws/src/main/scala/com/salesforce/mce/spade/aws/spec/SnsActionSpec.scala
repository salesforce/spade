package com.salesforce.mce.spade.aws.spec

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class SnsActionSpec(
  topicArn: String,
  subject: String,
  message: String
)

object SnsActionSpec {

  implicit val decoder: Decoder[SnsActionSpec] = deriveDecoder[SnsActionSpec]
  implicit val encoder: Encoder[SnsActionSpec] = deriveEncoder[SnsActionSpec]
}
