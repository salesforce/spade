package com.salesforce.mce.spade.aws.action

import java.util.UUID

import io.circe.syntax._

import com.salesforce.mce.spade.aws.action.SnsAlarm.ActionType
import com.salesforce.mce.spade.aws.spec.SnsActionSpec
import com.salesforce.mce.spade.workflow.Action

case class SnsAlarm private (
  topicArn: String,
  id: String,
  name: String,
  subject: String,
  message: String
) {

  def withSubject(s: String) = copy(subject = s)

  def withMessage(s: String) = copy(message = s)

  def asAction = Action(
    id,
    name,
    ActionType,
    SnsActionSpec(
      topicArn,
      subject,
      message
    ).asJson
  )
}

object SnsAlarm {

  final val ActionType = "aws.action.SnsAlarm"

  def apply(topicArn: String, name: String) = new SnsAlarm(
    topicArn,
    s"SnsAlarm-${UUID.randomUUID().toString}",
    name,
    s"Alert: $name",
    ""
  )
}