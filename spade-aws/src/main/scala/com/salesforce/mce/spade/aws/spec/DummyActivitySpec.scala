package com.salesforce.mce.spade.aws.spec

import io.circe._
import io.circe.generic.semiauto._

case class DummyActivitySpec(
  sleepSeconds: Int
)

object DummyActivitySpec {

  implicit val decoder: Decoder[DummyActivitySpec] = deriveDecoder[DummyActivitySpec]
  implicit val encoder: Encoder[DummyActivitySpec] = deriveEncoder[DummyActivitySpec]

}