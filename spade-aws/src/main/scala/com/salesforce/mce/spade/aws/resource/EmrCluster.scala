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
import com.salesforce.mce.spade.aws.spec.EmrResourceSpec.{InstanceTypeConfig, OnDemandProvisioningSpecification, SpotProvisioningSpecification}
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

  object SpotTimeoutAction extends Enumeration {
    final val SwitchToOnDemand = Value("SWITCH_TO_ON_DEMAND")
    final val TerminateCluster = Value("TERMINATE_CLUSTER")
  }

  object SpotAllocationStrategy extends Enumeration {
    final val CapacityOptimized = Value("capacity-optimized")
    final val PriceCapacityOptimized = Value("price-capacity-optimized")
    final val LowestPrice = Value("lowest-price")
    final val Diversified = Value("diversified")
    final val CapacityOptimizedPrioritized = Value("capacity-optimized-prioritized")
  }

  object OnDemandAllocationStrategy extends Enumeration {
    final val LowestPrice = Value("lowest-price")
    final val Prioritized = Value("prioritized")
  }

  case class BootstrapAction(path: String, args: String*)

  case class Builder(
    nameOpt: Option[String],
    applications: Seq[String],
    amiId: Option[String],
    enableInstanceFleet: Option[Boolean],
    subnetIds: Seq[String],
    instanceCountOpt: Option[Int],
    targetOnDemandCapacityOpt: Option[Int],
    targetSpotCapacityOpt: Option[Int],
    spotProvisioningSpecification: Option[SpotProvisioningSpecification],
    onDemandProvisioningSpecification: Option[OnDemandProvisioningSpecification],
    masterInstanceTypes: Seq[InstanceTypeConfig],
    coreInstanceTypes: Seq[InstanceTypeConfig],
    emrManagedMasterSecurityGroup: Option[String],
    emrManagedSlaveSecurityGroup: Option[String],
    additionalMasterSecurityGroupIds: Seq[String],
    additionalSlaveSecurityGroupIds: Seq[String],
    serviceAccessSecurityGroup: Option[String],
    bootstrapActions: Seq[BootstrapAction],
    configurations: Seq[EmrConfiguration],
    maxAttempt: Option[Int],
    terminateAfter: Option[Duration],
    useOnDemandOnLastAttempt: Option[Boolean]
  ) {

    def withName(name: String) = copy(nameOpt = Option(name))

    def withAmiId(amiId: String) = copy(amiId = Option(amiId))

    def withApplication(application: String) = copy(applications = applications :+ application)

    def withEnableInstanceFleet(enable: Boolean) = copy(enableInstanceFleet = Option(enable))

    def withSubnetId(subnetId: String) = copy(subnetIds = subnetIds :+ subnetId)

    def withSubnetIds(sIds: String*) = copy(subnetIds = subnetIds ++ sIds)

    def withInstanceCount(c: Int) = copy(instanceCountOpt = Option(c))

    def withTargetOnDemandCapacityOpt(c: Int) = copy(targetOnDemandCapacityOpt = Option(c))

    def withTargetSpotCapacityOpt(c: Int) = copy(targetSpotCapacityOpt = Option(c))

    def withSpotProvisioningSpecification(spec: SpotProvisioningSpecification) =
      copy(spotProvisioningSpecification = Option(spec))

    def withOnDemandProvisioningSpecification(spec: OnDemandProvisioningSpecification) =
      copy(onDemandProvisioningSpecification = Option(spec))

    def withMasterInstanceTypes(instanceTypes: Seq[InstanceTypeConfig]) =
      copy(masterInstanceTypes = masterInstanceTypes ++ instanceTypes)

    def withCoreInstanceTypes(instanceTypes: Seq[InstanceTypeConfig]) =
      copy(coreInstanceTypes = coreInstanceTypes ++ instanceTypes)

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

    def build()(implicit ctx: SpadeContext, sac: SpadeAwsContext): Resource[EmrCluster] = {

      val id = UUID.randomUUID().toString()
      val name = nameOpt.getOrElse(s"EmrCluster-$id")
      val enableFleet = enableInstanceFleet.contains(true)
      val instanceCount = instanceCountOpt.getOrElse(sac.emr.instanceCount)
      val instanceGroupConfigs =
        if (enableFleet) None
        else {
          val firstMasterInstanceOpt = masterInstanceTypes.headOption
          val firstCoreInstanceOpt = coreInstanceTypes.headOption
          Some(
            Seq(
              EmrResourceSpec.InstanceGroupConfig(
                s"${InstanceRoleType.Master}",
                1,
                firstMasterInstanceOpt.map(_.instanceType).getOrElse(sac.emr.masterInstanceType),
                firstMasterInstanceOpt.flatMap(_.bidPrice),
              ),
              EmrResourceSpec.InstanceGroupConfig(
                s"${InstanceRoleType.Core}",
                scala.math.max(instanceCount - 1, 1),
                firstCoreInstanceOpt.map(_.instanceType).getOrElse(sac.emr.coreInstanceType),
                firstCoreInstanceOpt.flatMap(_.bidPrice)
              )
            )
          )
        }

      val defaultSpotProvisionSpec = SpotProvisioningSpecification(
        s"${SpotTimeoutAction.SwitchToOnDemand}",
        sac.emr.defaultSpotTimeoutDuration,
        Some(s"${SpotAllocationStrategy.CapacityOptimized}")
      )

      val defaultOnDemandProvisionSpec = OnDemandProvisioningSpecification(
        s"${OnDemandAllocationStrategy.LowestPrice}"
      )

      val instanceFleetConfigs ={
        if (enableFleet) {
          Some(
            Seq(
              EmrResourceSpec.InstanceFleetConfig(
                s"${InstanceRoleType.Master}",
                1,
                targetSpotCapacityOpt.map(_ => 1),
                spotProvisioningSpecification.orElse(Some(defaultSpotProvisionSpec)),
                onDemandProvisioningSpecification.orElse(Some(defaultOnDemandProvisionSpec)),
                masterInstanceTypes.map(r => InstanceTypeConfig(r.instanceType, r.bidPrice, None))
              ),
              EmrResourceSpec.InstanceFleetConfig(
                s"${InstanceRoleType.Core}",
                scala.math.max(targetOnDemandCapacityOpt.getOrElse(sac.emr.targetCapacity) - 1, 1),
                targetSpotCapacityOpt.map(r => scala.math.max(r - 1, 1)),
                spotProvisioningSpecification.orElse(Some(defaultSpotProvisionSpec)),
                onDemandProvisioningSpecification.orElse(Some(defaultOnDemandProvisionSpec)),
                coreInstanceTypes.map(r => InstanceTypeConfig(r.instanceType, r.bidPrice, r.weightedCapacity))
              )
            )
          )
        } else None
      }

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
            if (subnetIds.nonEmpty) Some(subnetIds.head) else Some(sac.emr.subnetId),
            if (subnetIds.nonEmpty) Some(subnetIds) else Some(Seq(sac.emr.subnetId)),
            sac.emr.ec2KeyName,
            instanceGroupConfigs,
            instanceFleetConfigs,
            emrManagedMasterSecurityGroup,
            emrManagedSlaveSecurityGroup,
            additionalMasterSecurityGroupIds.asOption(),
            additionalSlaveSecurityGroupIds.asOption(),
            serviceAccessSecurityGroup
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
    enableInstanceFleet = None,
    subnetIds = Seq.empty,
    instanceCountOpt = None,
    targetOnDemandCapacityOpt = None,
    targetSpotCapacityOpt = None,
    spotProvisioningSpecification = None,
    onDemandProvisioningSpecification = None,
    masterInstanceTypes = Seq.empty,
    coreInstanceTypes = Seq.empty,
    emrManagedMasterSecurityGroup = None,
    emrManagedSlaveSecurityGroup = None,
    additionalMasterSecurityGroupIds = Seq.empty,
    additionalSlaveSecurityGroupIds = Seq.empty,
    serviceAccessSecurityGroup = None,
    bootstrapActions = Seq.empty,
    configurations = Seq.empty,
    maxAttempt = None,
    terminateAfter = None,
    useOnDemandOnLastAttempt = None
  )
}
