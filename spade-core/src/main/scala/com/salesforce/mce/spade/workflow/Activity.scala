package com.salesforce.mce.spade.workflow

import io.circe.Json

case class Activity[R](
  id: String,
  name: String,
  activityType: String,
  activitySpec: Json,
  runsOn: Resource[R],
  maxAttempt: Option[Int]
)
