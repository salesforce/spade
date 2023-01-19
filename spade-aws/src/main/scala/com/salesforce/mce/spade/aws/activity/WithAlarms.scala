package com.salesforce.mce.spade.aws.activity

import com.salesforce.mce.spade.aws.action.SnsAlarm
import com.salesforce.mce.spade.aws.activity.WithAlarms.AlarmFields

trait WithAlarms {

  type Self <: WithAlarms

  def alarmFields: AlarmFields
  def updateAlarmFields(fields: AlarmFields): Self

  def onSuccess(alarms: SnsAlarm*): Self = updateAlarmFields(
    alarmFields.copy(
      onSuccessAlarms = Option(alarms)
    )
  )

  def onFail(alarms: SnsAlarm*): Self = updateAlarmFields(
    alarmFields.copy(
      onFailAlarms = Option(alarms)
    )
  )
}

object WithAlarms {

  case class AlarmFields(
    onSuccessAlarms: Option[Seq[SnsAlarm]] = None,
    onFailAlarms: Option[Seq[SnsAlarm]] = None
  ) {

    def onSuccessActions = onSuccessAlarms.map(_.map(_.asAction))
    def onFailActions = onFailAlarms.map(_.map(_.asAction))
  }

}
