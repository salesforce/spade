/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.aws.resource

import java.util.UUID

import io.circe.syntax._

import com.salesforce.mce.spade.SpadeContext
import com.salesforce.mce.spade.aws.SpadeAwsContext
import com.salesforce.mce.spade.aws.spec.{AwsTag, EmrResourceSpec}
import com.salesforce.mce.spade.workflow.Resource

trait EmrCluster

object EmrCluster {

  final val ResourceType = "aws.resource.EmrResource"

  case class Builder(
    nameOpt: Option[String],
    applications: Seq[String],
    instanceCountOpt: Option[Int],
    maxAttempt: Option[Int]
  ) {

    def withName(name: String) = copy(nameOpt = Option(name))
    def withApplication(application: String) = copy(applications = applications :+ application)
    def withInstanceCount(c: Int) = copy(instanceCountOpt = Option(c))
    def withMaxAttempt(n: Int) = copy(maxAttempt = Option(n))

    def build()(implicit ctx: SpadeContext, sac: SpadeAwsContext): Resource[EmrCluster] = {

      val id = UUID.randomUUID().toString()
      val name = nameOpt.getOrElse(s"EmrCluster-$id")
      val instanceCount = instanceCountOpt.getOrElse(sac.emr.instanceCount)

      Resource[EmrCluster](
        id,
        name,
        ResourceType,
        EmrResourceSpec(
          sac.emr.releaseLabel,
          applications,
          sac.emr.serviceRole,
          sac.emr.resourceRole,
          Option(sac.emr.tags.map { case (k, v) => AwsTag(k, v) }),
          EmrResourceSpec.InstancesConfig(
            sac.emr.subnetId,
            sac.emr.ec2KeyName,
            instanceCount,
            sac.emr.masterInstanceType,
            sac.emr.slaveInstanceType
          )
        ).asJson,
        maxAttempt.getOrElse(ctx.maxAttempt)
      )
    }
  }

  def builder(): EmrCluster.Builder = Builder(None, Seq.empty, None, None)

}
