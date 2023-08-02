package com.salesforce.mce.spade.aws.spec

import io.circe._
import io.circe.generic.semiauto._

case class Ec2ResourceSpec(
  amiImageId: String,
  subnetId: String,
  instanceType: String,
  instanceProfile: String,
  securityGroups: Option[Seq[String]],
  tags: Option[Seq[AwsTag]],
  name: Option[String],
  spotInstance: Boolean
)

object Ec2ResourceSpec {

  implicit val decoder: Decoder[Ec2ResourceSpec] = deriveDecoder[Ec2ResourceSpec]
  implicit val encoder: Encoder[Ec2ResourceSpec] = deriveEncoder[Ec2ResourceSpec]
}
