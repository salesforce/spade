/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.aws.activity

import java.util.UUID

import io.circe.syntax._

import com.salesforce.mce.spade.aws.resource.EmrCluster
import com.salesforce.mce.spade.aws.spec.EmrActivitySpec
import com.salesforce.mce.spade.workflow.{Activity, Resource}
import com.salesforce.mce.spade.SpadeContext

trait EmrActivity

object EmrActivity {

  final val ActivityType = "aws.activity.EmrActivity"

  case class Builder(
    nameOpt: Option[String],
    steps: Seq[EmrActivitySpec.Step],
    runsOn: Resource[EmrCluster],
    maxAttempty: Option[Int]
  ) {

    def withName(name: String) = copy(nameOpt = Option(name))
    def withSteps(moreSteps: EmrActivitySpec.Step*) = copy(steps = steps ++ moreSteps)
    def withMaxAttempty(n: Int) = copy(maxAttempty = Option(n))

    def build()(implicit ctx: SpadeContext): Activity[EmrCluster] = {

      val id = UUID.randomUUID().toString()
      val name = nameOpt.getOrElse(s"EmrActivity-$id")

      Activity(
        id,
        name,
        ActivityType,
        EmrActivitySpec(steps).asJson,
        runsOn,
        maxAttempty.getOrElse(ctx.maxAttempt)
      )
    }

  }

  def builder(emrCluster: Resource[EmrCluster]) = Builder(None, Seq.empty, emrCluster, None)
}
