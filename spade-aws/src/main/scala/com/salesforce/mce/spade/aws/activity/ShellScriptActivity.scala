package com.salesforce.mce.spade.aws.activity

import java.util.UUID
import io.circe.syntax._

import com.salesforce.mce.spade.aws.resource.Ec2Instance
import com.salesforce.mce.spade.workflow.{Activity, Resource}
import com.salesforce.mce.spade.SpadeContext
import com.salesforce.mce.spade.aws.spec.ShellScriptActivitySpec

object ShellScriptActivity {

  final val ActivityType = "aws.activity.ShellScriptActivity"

  case class Builder(
    nameOpt: Option[String],
    scriptLocation: String,
    args: Seq[String],
    outputS3BucketName: String,
    outputS3KeyPrefix: String,
    runsOn: Resource[Ec2Instance],
    executionTimeout: Option[Int],
    deliveryTimeout: Option[Int],
    maxAttempt: Option[Int]
  ) {

    def withName(name: String) = copy(nameOpt = Option(name))
    def withScriptLocation(location: String) = copy(scriptLocation = location)
    def withArgs(arguments: String*) = copy(args = arguments)
    def withOutputS3BucketName(name: String) = copy(outputS3BucketName = name)
    def withOutputS3KeyPrefix(name: String) = copy(outputS3KeyPrefix = name)
    def withExecutionTimeout(timeout: Int) = copy(executionTimeout = Option(timeout))
    def withDeliveryTimeout(timeout: Int) = copy(deliveryTimeout = Option(timeout))
    def withMaxAttempt(n: Int) = copy(maxAttempt = Option(n))

    def build()(implicit ctx: SpadeContext): Activity[Ec2Instance] = {

      val id = UUID.randomUUID().toString()
      val name = nameOpt.getOrElse(s"ShellScriptActivity-$id")

      Activity(
        id,
        name,
        ActivityType,
        ShellScriptActivitySpec(
          scriptLocation,
          args,
          outputS3BucketName,
          outputS3KeyPrefix,
          executionTimeout.getOrElse(ctx.executionTimeout),
          deliveryTimeout.getOrElse(ctx.deliveryTimeout)
        ).asJson,
        runsOn,
        maxAttempt.getOrElse(ctx.maxAttempt)
      )
    }

  }

  def builder(
    ec2Instance: Resource[Ec2Instance],
    scriptLocation: String,
    outputS3BucketName: String,
    outputS3KeyPrefix: String
  ) =
    Builder(None, scriptLocation, Seq.empty, outputS3BucketName, outputS3KeyPrefix, ec2Instance, None, None, None)
}
