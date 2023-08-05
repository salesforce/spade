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
  bootstrapActions: Option[Seq[EmrResourceSpec.BootstrapAction]],
  configurations: Option[Seq[EmrResourceSpec.Configuration]],
  instancesConfig: EmrResourceSpec.InstancesConfig
)

object EmrResourceSpec {

  case class InstancesConfig(
    subnetId: String,
    ec2KeyName: Option[String],
    instanceGroupConfigs: Option[Seq[InstanceGroupConfig]],
    additionalMasterSecurityGroups: Option[Seq[String]],
    additionalSlaveSecurityGroups: Option[Seq[String]]
  )

  case class InstanceGroupConfig(
    instanceRoleType: String,
    instanceCount: Int,
    instanceType: String,
    instanceBidPrice: Option[String]
  )
  implicit val igcDecoder: Decoder[InstanceGroupConfig] = deriveDecoder[InstanceGroupConfig]
  implicit val igcEncoder: Encoder[InstanceGroupConfig] = deriveEncoder[InstanceGroupConfig]

  implicit val icDecoder: Decoder[InstancesConfig] = deriveDecoder[InstancesConfig]
  implicit val icEncoder: Encoder[InstancesConfig] = deriveEncoder[InstancesConfig]

  case class BootstrapAction(path: String, args: Seq[String])
  implicit val baDecoder: Decoder[BootstrapAction] = deriveDecoder[BootstrapAction]
  implicit val baEncoder: Encoder[BootstrapAction] = deriveEncoder[BootstrapAction]

  case class Configuration(
    classification: String,
    properties: Option[Map[String, String]],
    configurations: Option[Seq[Configuration]]
  )
  implicit val confDecoder: Decoder[Configuration] = deriveDecoder[Configuration]
  implicit val confEncoder: Encoder[Configuration] = deriveEncoder[Configuration]

  implicit val decoder: Decoder[EmrResourceSpec] = deriveDecoder[EmrResourceSpec]
  implicit val encoder: Encoder[EmrResourceSpec] = deriveEncoder[EmrResourceSpec]

}
