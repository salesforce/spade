package com.salesforce.mce.spade.aws.activity

import java.util.UUID
import io.circe.syntax._

import com.salesforce.mce.spade.aws.resource.Ec2Instance
import com.salesforce.mce.spade.workflow.{Activity, Resource}
import com.salesforce.mce.spade.SpadeContext
import com.salesforce.mce.spade.aws.activity.WithAlarms.AlarmFields
import com.salesforce.mce.spade.aws.spec.ShellCommandActivitySpec

object ShellCommandActivity {

  final val ActivityType = "aws.activity.ShellCommandActivity"

  case class Builder(
    nameOpt: Option[String],
    lines: Seq[String],
    outputUri: Option[String],
    runsOn: Resource[Ec2Instance],
    executionTimeout: Option[Int],
    deliveryTimeout: Option[Int],
    maxAttempt: Option[Int],
    alarmFields: AlarmFields
  ) extends WithAlarms {

    type Self = Builder

    def withName(name: String) = copy(nameOpt = Option(name))
    def withLines(args: String*) = copy(lines = args)
    def withOutputUri(uri: String) = copy(outputUri = Option(uri))
    def withExecutionTimeout(timeout: Int) = copy(executionTimeout = Option(timeout))
    def withDeliveryTimeout(timeout: Int) = copy(deliveryTimeout = Option(timeout))
    def withMaxAttempt(n: Int) = copy(maxAttempt = Option(n))
    def updateAlarmFields(fields: AlarmFields) = copy(alarmFields = fields)

    def build()(implicit ctx: SpadeContext): Activity[Ec2Instance] = {

      val id = UUID.randomUUID().toString()
      val name = nameOpt.getOrElse(s"ShellCommandActivity-$id")

      Activity(
        id,
        name,
        ActivityType,
        ShellCommandActivitySpec(
          lines,
          outputUri,
          executionTimeout,
          deliveryTimeout
        ).asJson,
        runsOn,
        maxAttempt.getOrElse(ctx.maxAttempt),
        alarmFields.onSuccessActions,
        alarmFields.onFailActions
      )
    }

  }

  def builder(ec2Instance: Resource[Ec2Instance]) =
    Builder(None, Seq.empty, None, ec2Instance, None, None, None, AlarmFields())
}
