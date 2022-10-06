package com.salesforce.mce.spade.workflow

import io.circe.Json

case class Resource[+R](
  id: String,
  name: String,
  resourceType: String,
  resourceSpec: Json,
  maxAttempt: Option[Int]
)
