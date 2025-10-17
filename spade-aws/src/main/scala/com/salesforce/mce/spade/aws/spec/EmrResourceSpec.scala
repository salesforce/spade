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
  customAmiId: Option[String],
  applications: Seq[String],
  serviceRole: String,
  resourceRole: String,
  tags: Option[Seq[AwsTag]],
  bootstrapActions: Option[Seq[EmrResourceSpec.BootstrapAction]],
  configurations: Option[Seq[EmrResourceSpec.Configuration]],
  instancesConfig: EmrResourceSpec.InstancesConfig,
  useOnDemandOnLastAttempt: Option[Boolean]
)

object EmrResourceSpec {

  case class InstancesConfig(
    subnetId: Option[String],
    subnetIds: Option[Seq[String]],
    ec2KeyName: Option[String],
    instanceGroupConfigs: Option[Seq[InstanceGroupConfig]],
    instanceFleetConfigs: Option[Seq[InstanceFleetConfig]],
    emrManagedMasterSecurityGroup: Option[String],
    emrManagedSlaveSecurityGroup: Option[String],
    additionalMasterSecurityGroups: Option[Seq[String]],
    additionalSlaveSecurityGroups: Option[Seq[String]],
    serviceAccessSecurityGroup: Option[String]
  )

  case class InstanceGroupConfig(
    instanceRoleType: String,
    instanceCount: Int,
    instanceType: String,
    instanceBidPrice: Option[String]
  )

  case class InstanceTypeConfig(
    instanceType: String,
    bidPrice: Option[String],
    weightedCapacity: Option[Int]
  )

  case class SpotProvisioningSpecification(
    timeoutAction: String,
    timeoutDurationMinutes: Int,
    allocationStrategy: Option[String]
  )

  case class OnDemandProvisioningSpecification(allocationStrategy: String)

  case class InstanceFleetConfig(
    instanceRoleType: String,
    targetOnDemandCapacity: Int,
    targetSpotCapacity: Option[Int],
    spotProvisioningSpecification: Option[SpotProvisioningSpecification],
    onDemandProvisioningSpecification: Option[OnDemandProvisioningSpecification],
    instanceConfigs: Seq[InstanceTypeConfig]
  )

  implicit val igcDecoder: Decoder[InstanceGroupConfig] = deriveDecoder[InstanceGroupConfig]
  implicit val igcEncoder: Encoder[InstanceGroupConfig] = deriveEncoder[InstanceGroupConfig]

  implicit val itcDecoder: Decoder[InstanceTypeConfig] = deriveDecoder[InstanceTypeConfig]
  implicit val itcEncoder: Encoder[InstanceTypeConfig] = deriveEncoder[InstanceTypeConfig]

  implicit val iscDecoder: Decoder[InstancesConfig] = deriveDecoder[InstancesConfig]
  implicit val iscEncoder: Encoder[InstancesConfig] = deriveEncoder[InstancesConfig]

  implicit val spsDecoder: Decoder[SpotProvisioningSpecification] = deriveDecoder[SpotProvisioningSpecification]
  implicit val spsEncoder: Encoder[SpotProvisioningSpecification] = deriveEncoder[SpotProvisioningSpecification]

  implicit val odpsDecoder: Decoder[OnDemandProvisioningSpecification] = deriveDecoder[OnDemandProvisioningSpecification]
  implicit val odpsEncoder: Encoder[OnDemandProvisioningSpecification] = deriveEncoder[OnDemandProvisioningSpecification]

  implicit val ifcDecoder: Decoder[InstanceFleetConfig] = deriveDecoder[InstanceFleetConfig]
  implicit val ifcEncoder: Encoder[InstanceFleetConfig] = deriveEncoder[InstanceFleetConfig]

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
