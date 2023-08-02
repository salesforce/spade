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
    subnetId: Option[String],
    instanceCountOpt: Option[Int],
    masterInstanceType: Option[String],
    coreInstanceType: Option[String],
    masterInstanceBidPrice: Option[String],
    coreInstanceBidPrice: Option[String],
    additionalMasterSecurityGroupIds: Seq[String],
    additionalSlaveSecurityGroupIds: Seq[String],
    bootstrapActions: Seq[BootstrapAction],
    configurations: Seq[EmrConfiguration],
    maxAttempt: Option[Int],
    terminateAfter: Option[Duration]
  ) {

    def withName(name: String) = copy(nameOpt = Option(name))

    def withApplication(application: String) = copy(applications = applications :+ application)

    def withSubnetId(subnetId: String) = copy(subnetId = Option(subnetId))

    def withInstanceCount(c: Int) = copy(instanceCountOpt = Option(c))

    def withMasterInstanceType(instType: String) = copy(masterInstanceType = Option(instType))

    def withCoreInstanceType(instType: String) = copy(coreInstanceType = Option(instType))

    def withMasterInstanceBidPrice(bidPrice: Double) = copy(masterInstanceBidPrice = Option(s"$bidPrice"))

    def withCoreInstanceBidPrice(bidPrice: Double) = copy(coreInstanceBidPrice = Option(s"$bidPrice"))

    def withAdditionalMasterSecurityGroupIds(groupIds: String*) =
      copy(additionalMasterSecurityGroupIds = additionalMasterSecurityGroupIds ++ groupIds)

    def withAdditionalSlaveSecurityGroupIds(groupIds: String*) =
      copy(additionalSlaveSecurityGroupIds = additionalSlaveSecurityGroupIds ++ groupIds)

    def withBootstrapActions(bas: BootstrapAction*) =
      copy(bootstrapActions = bootstrapActions ++ bas)

    def withConfigurations(cs: EmrConfiguration*) =
      copy(configurations = configurations ++ cs)

    def withMaxAttempt(n: Int) = copy(maxAttempt = Option(n))

    def withTerminateAfter(duration: Duration) = copy(terminateAfter = Option(duration))

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

      Resource[EmrCluster](
        id,
        name,
        ResourceType,
        EmrResourceSpec(
          sac.emr.releaseLabel,
          applications,
          sac.emr.serviceRole,
          sac.emr.resourceRole,
          Option((sac.tags.toSeq ++ sac.emr.tags).distinct.map { case (k, v) => AwsTag(k, v) }),
          bootstrapActions.map(ba => EmrResourceSpec.BootstrapAction(ba.path, ba.args)).asOption(),
          configurations.map(_.asSpec()).asOption(),
          nameOpt,
          EmrResourceSpec.InstancesConfig(
            subnetId.getOrElse(sac.emr.subnetId),
            instanceCount,
            sac.emr.ec2KeyName,
            Some(instanceGroupConfigs),
            additionalMasterSecurityGroupIds.asOption(),
            additionalSlaveSecurityGroupIds.asOption()
          )
        ).asJson,
        maxAttempt.getOrElse(ctx.maxAttempt),
        terminateAfter
      )
    }
  }

  def builder(): EmrCluster.Builder = Builder(
    None, Seq.empty, None, None, None, None, None, None, Seq.empty, Seq.empty, Seq.empty, Seq.empty, None, None
  )
}
