package com.salesforce.mce.spade.workflow

import io.circe.Json

case class Action(
  id: String,
  name: String,
  actionType: String,
  actionSpec: Json
)
