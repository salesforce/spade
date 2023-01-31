package com.salesforce.mce.spade.aws.activity

import java.util.UUID
import io.circe.syntax._

import com.salesforce.mce.spade.aws.resource.DummyResource
import com.salesforce.mce.spade.workflow.{Activity, Resource}
import com.salesforce.mce.spade.SpadeContext
import com.salesforce.mce.spade.aws.activity.WithAlarms.AlarmFields
import com.salesforce.mce.spade.aws.spec.DummyActivitySpec

object DummyActivity {

  final val ActivityType = "v.DummyActivity"

  case class Builder(
    nameOpt: Option[String],
    sleepSecondsOpt: Option[Int],
    runsOn: Resource[DummyResource],
    alarmFields: AlarmFields
  ) extends WithAlarms {

    type Self = Builder

    def withName(name: String) = copy(nameOpt = Option(name))
    def withSleepSeconds(seconds: Int) = copy(sleepSecondsOpt = Option(seconds))
    def updateAlarmFields(fields: AlarmFields) = copy(alarmFields = fields)

    def build()(implicit ctx: SpadeContext): Activity[DummyResource] = {

      val id = UUID.randomUUID().toString()
      val name = nameOpt.getOrElse(s"DummyActivity-$id")

      Activity(
        id,
        name,
        ActivityType,
        DummyActivitySpec(
          sleepSecondsOpt.getOrElse(10)
        ).asJson,
        runsOn,
        ctx.maxAttempt,
        alarmFields.onSuccessActions,
        alarmFields.onFailActions
      )
    }

  }

  def builder(dummyResource: Resource[DummyResource]) =
    Builder(None, None, dummyResource, AlarmFields())
}
