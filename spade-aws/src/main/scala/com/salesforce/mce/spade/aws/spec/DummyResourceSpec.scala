package com.salesforce.mce.spade.aws.spec

import io.circe._
import io.circe.generic.semiauto._

case class DummyResourceSpec(
  initSeconds: Int
)

object DummyResourceSpec {

  implicit val decoder: Decoder[DummyResourceSpec] = deriveDecoder[DummyResourceSpec]
  implicit val encoder: Encoder[DummyResourceSpec] = deriveEncoder[DummyResourceSpec]
}
