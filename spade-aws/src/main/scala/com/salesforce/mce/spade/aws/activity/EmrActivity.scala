package com.salesforce.mce.spade.aws.activity

import java.util.UUID

import io.circe.syntax._

import com.salesforce.mce.spade.aws.resource.EmrCluster
import com.salesforce.mce.spade.aws.spec.EmrActivitySpec
import com.salesforce.mce.spade.workflow.{Activity, Resource}

trait EmrActivity

object EmrActivity {

  final val ActivityType = "aws.activity.EmrActivity"

  case class Builder(
    nameOpt: Option[String],
    steps: Seq[EmrActivitySpec.Step],
    runsOn: Resource[EmrCluster]
  ) {

    def withName(name: String) = copy(nameOpt = Option(name))
    def withSteps(moreSteps: EmrActivitySpec.Step*) = copy(steps = steps ++ moreSteps)

    def build(): Activity[EmrCluster] = {

      val id = UUID.randomUUID().toString()
      val name = nameOpt.getOrElse(s"EmrActivity-$id")

      Activity(
        id,
        name,
        ActivityType,
        EmrActivitySpec(steps).asJson,
        runsOn,
        None
      )
    }

  }

  def builder(emrCluster: Resource[EmrCluster]) = Builder(None, Seq.empty, emrCluster)
}
