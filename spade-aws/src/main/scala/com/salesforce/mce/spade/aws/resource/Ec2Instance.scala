package com.salesforce.mce.spade.aws.resource

import java.util.UUID

import io.circe.syntax._

import com.salesforce.mce.spade.SpadeContext
import com.salesforce.mce.spade.aws.SpadeAwsContext
import com.salesforce.mce.spade.aws.spec.{AwsTag, Ec2ResourceSpec}
import com.salesforce.mce.spade.workflow.Resource

trait Ec2Instance

object Ec2Instance {

  final val ResourceType = "aws.resource.Ec2Resource"

  case class BootstrapAction(path: String, args: String*)

  case class Builder(
    nameOpt: Option[String],
    instanceType: Option[String],
    securityGroupIds: Option[Seq[String]],
    spotInstance: Option[Boolean],
    maxAttempt: Option[Int],
    terminateAfter: Option[Double]
  ) {

    def withName(name: String) = copy(nameOpt = Option(name))

    def withInstanceType(instType: String) = copy(instanceType = Option(instType))

    def withSecurityGroupIds(groupIds: String*) = copy(securityGroupIds = Option(groupIds))

    def withMaxAttempt(n: Int) = copy(maxAttempt = Option(n))

    def withTerminateAfter(hours: Double) = copy(terminateAfter = Option(hours))

    def build()(implicit ctx: SpadeContext, sac: SpadeAwsContext): Resource[Ec2Instance] = {

      val id = UUID.randomUUID().toString()
      val name = nameOpt.getOrElse(s"Ec2Instance-$id")

      Resource[Ec2Instance](
        id,
        name,
        ResourceType,
        Ec2ResourceSpec(
          sac.ec2.amiImageId,
          sac.ec2.subnetId,
          instanceType.getOrElse(sac.ec2.instanceType),
          sac.ec2.instanceProfile,
          securityGroupIds,
          Option((sac.tags.toSeq ++ sac.ec2.tags).distinct.map { case (k, v) => AwsTag(k, v) }),
          spotInstance.getOrElse(sac.ec2.spotInstance)
        ).asJson,
        maxAttempt.getOrElse(ctx.maxAttempt),
        terminateAfter
      )
    }
  }

  def builder(): Ec2Instance.Builder =
    Builder(None, None, None, None, None, None)

}
