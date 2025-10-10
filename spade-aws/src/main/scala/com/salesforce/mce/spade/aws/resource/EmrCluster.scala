/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.aws.resource

import java.time.Duration
import java.util.UUID

import io.circe.syntax._

import com.salesforce.mce.spade.SpadeContext
import com.salesforce.mce.spade.aws.SpadeAwsContext
import com.salesforce.mce.spade.aws.spec.{AwsTag, EmrResourceSpec}
import com.salesforce.mce.spade.workflow.Resource
import com.salesforce.mce.spade.aws.util._

trait EmrCluster

object EmrCluster {

  final val ResourceType = "aws.resource.EmrResource"

  object InstanceRoleType extends Enumeration {
    final val Master = Value("MASTER")
    final val Core = Value("CORE")
    final val Task = Value("TASK")
  }

  case class BootstrapAction(path: String, args: String*)

  case class Builder(
    nameOpt: Option[String],
    applications: Seq[String],
    amiId: Option[String],
    subnetIds: Seq[String],
    useEmrInstanceFleet: Option[Boolean],
    instanceCountOpt: Option[Int],
    masterInstanceType: Option[String],
    coreInstanceType: Option[String],
    masterInstanceBidPrice: Option[String],
    coreInstanceBidPrice: Option[String],
    emrManagedMasterSecurityGroup: Option[String],
    emrManagedSlaveSecurityGroup: Option[String],
    additionalMasterSecurityGroupIds: Seq[String],
    additionalSlaveSecurityGroupIds: Seq[String],
    serviceAccessSecurityGroup: Option[String],
    bootstrapActions: Seq[BootstrapAction],
    configurations: Seq[EmrConfiguration],
    maxAttempt: Option[Int],
    terminateAfter: Option[Duration],
    useOnDemandOnLastAttempt: Option[Boolean],
    spotAllocationStrategy: Option[String],
    onDemandAllocationStrategy: Option[String]
  ) {

    def withName(name: String) = copy(nameOpt = Option(name))

    def withAmiId(amiId: String) = copy(amiId = Option(amiId))

    def withApplication(application: String) = copy(applications = applications :+ application)

    def withSubnetIds(ids: String*) = copy(subnetIds = subnetIds ++ ids)

    def withUseEmrInstanceFleet(use: Boolean) = copy(useEmrInstanceFleet = Option(use))

    def withInstanceCount(c: Int) = copy(instanceCountOpt = Option(c))

    def withMasterInstanceType(instType: String) = copy(masterInstanceType = Option(instType))

    def withCoreInstanceType(instType: String) = copy(coreInstanceType = Option(instType))

    def withMasterInstanceBidPrice(bidPrice: Double) = copy(masterInstanceBidPrice = Option(s"$bidPrice"))

    def withCoreInstanceBidPrice(bidPrice: Double) = copy(coreInstanceBidPrice = Option(s"$bidPrice"))

    def withManagedMasterSecurityGroupId(groupId: String) = copy(emrManagedMasterSecurityGroup = Option(groupId))

    def withManagedSlaveSecurityGroupId(groupId: String) = copy(emrManagedSlaveSecurityGroup = Option(groupId))

    def withAdditionalMasterSecurityGroupIds(groupIds: String*) =
      copy(additionalMasterSecurityGroupIds = additionalMasterSecurityGroupIds ++ groupIds)

    def withAdditionalSlaveSecurityGroupIds(groupIds: String*) =
      copy(additionalSlaveSecurityGroupIds = additionalSlaveSecurityGroupIds ++ groupIds)

    def withServiceAccessSecurityGroupId(groupId: String) = copy(serviceAccessSecurityGroup = Option(groupId))

    def withBootstrapActions(bas: BootstrapAction*) =
      copy(bootstrapActions = bootstrapActions ++ bas)

    def withConfigurations(cs: EmrConfiguration*) =
      copy(configurations = configurations ++ cs)

    def withMaxAttempt(n: Int) = copy(maxAttempt = Option(n))

    def withTerminateAfter(duration: Duration) = copy(terminateAfter = Option(duration))

    def withOnDemandOnLastAttempt(use: Boolean) = copy(useOnDemandOnLastAttempt = Option(use))

    def withSpotAllocationStrategy(strategy: String) = copy(spotAllocationStrategy = Option(strategy))

    def withOnDemandAllocationStrategy(strategy: String) = copy(onDemandAllocationStrategy = Option(strategy))

    def build()(implicit ctx: SpadeContext, sac: SpadeAwsContext): Resource[EmrCluster] = {

      val id = UUID.randomUUID().toString()
      val name = nameOpt.getOrElse(s"EmrCluster-$id")
      val instanceCount = instanceCountOpt.getOrElse(sac.emr.instanceCount)
      val instanceGroupConfigs = Seq(
        EmrResourceSpec.InstanceGroupConfig(
          s"${InstanceRoleType.Master}",
          1,
          masterInstanceType.getOrElse(sac.emr.masterInstanceType),
          masterInstanceBidPrice
        ),
        EmrResourceSpec.InstanceGroupConfig(
          s"${InstanceRoleType.Core}",
          scala.math.max(instanceCount - 1, 1),
          coreInstanceType.getOrElse(sac.emr.coreInstanceType),
          coreInstanceBidPrice
        )
      )
      val instanceFleetConfigs = Seq(
        EmrResourceSpec.InstanceFleetConfig(
          s"${InstanceRoleType.Master}",
          1,
          Seq(EmrResourceSpec.InstanceConfig(
            masterInstanceType.getOrElse(sac.emr.masterInstanceType),
            masterInstanceBidPrice,
            None),
          )
        ),
        EmrResourceSpec.InstanceFleetConfig(
          s"${InstanceRoleType.Core}",
          scala.math.max(instanceCount - 1, 1),
          Seq(EmrResourceSpec.InstanceConfig(
            coreInstanceType.getOrElse(sac.emr.coreInstanceType),
            coreInstanceBidPrice,
            None),
          )
        )
      )


      Resource[EmrCluster](
        id,
        name,
        ResourceType,
        EmrResourceSpec(
          sac.emr.releaseLabel,
          amiId.orElse(sac.emr.customAmiId),
          applications,
          sac.emr.serviceRole,
          sac.emr.resourceRole,
          Option((sac.tags.toSeq ++ sac.emr.tags).distinct.map { case (k, v) => AwsTag(k, v) }),
          bootstrapActions.map(ba => EmrResourceSpec.BootstrapAction(ba.path, ba.args)).asOption(),
          configurations.map(_.asSpec()).asOption(),
          EmrResourceSpec.InstancesConfig(
            if (subnetIds.nonEmpty) subnetIds else sac.emr.subnetIds,
            sac.emr.ec2KeyName,
            useEmrInstanceFleet,
            if (useEmrInstanceFleet.contains(true)) None else Some(instanceGroupConfigs),
            if (useEmrInstanceFleet.contains(true)) Some(instanceFleetConfigs) else None,
            emrManagedMasterSecurityGroup,
            emrManagedSlaveSecurityGroup,
            additionalMasterSecurityGroupIds.asOption(),
            additionalSlaveSecurityGroupIds.asOption(),
            serviceAccessSecurityGroup,
            spotAllocationStrategy,
            onDemandAllocationStrategy
          ),
          useOnDemandOnLastAttempt
        ).asJson,
        maxAttempt.getOrElse(ctx.maxAttempt),
        terminateAfter
      )
    }
  }

  def builder(): EmrCluster.Builder = Builder(
    nameOpt = None,
    applications = Seq.empty,
    amiId = None,
    subnetIds = Seq.empty,
    useEmrInstanceFleet = None,
    instanceCountOpt = None,
    masterInstanceType = None,
    coreInstanceType = None,
    masterInstanceBidPrice = None,
    coreInstanceBidPrice = None,
    emrManagedMasterSecurityGroup = None,
    emrManagedSlaveSecurityGroup = None,
    additionalMasterSecurityGroupIds = Seq.empty,
    additionalSlaveSecurityGroupIds = Seq.empty,
    serviceAccessSecurityGroup = None,
    bootstrapActions = Seq.empty,
    configurations = Seq.empty,
    maxAttempt = None,
    terminateAfter = None,
    useOnDemandOnLastAttempt = None,
    spotAllocationStrategy = None,
    onDemandAllocationStrategy = None
  )
}
