/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.aws.spec

import io.circe._
import io.circe.generic.semiauto._

case class EmrResourceSpec(
  releaseLabel: String,
  applications: Seq[String],
  serviceRole: String,
  resourceRole: String,
  tags: Option[Seq[AwsTag]],
  instancesConfig: EmrResourceSpec.InstancesConfig
)

object EmrResourceSpec {

  case class InstancesConfig(
    subnetId: String,
    ec2KeyName: String,
    instanceCount: Int,
    masterInstanceType: String,
    slaveInstanceType: String,
    additionalMasterSecurityGroups: Option[Seq[String]],
    additionalSlaveSecurityGroups: Option[Seq[String]]
  )

  implicit val icDecoder: Decoder[InstancesConfig] = deriveDecoder[InstancesConfig]
  implicit val icEncoder: Encoder[InstancesConfig] = deriveEncoder[InstancesConfig]

  implicit val decoder: Decoder[EmrResourceSpec] = deriveDecoder[EmrResourceSpec]
  implicit val encoder: Encoder[EmrResourceSpec] = deriveEncoder[EmrResourceSpec]

}
