package com.salesforce.mce.spade.orchard

import java.time.LocalDateTime

import io.circe.generic.semiauto._

case class WorkflowResponse(
  id: String,
  name: String,
  status: String,
  createdAt: LocalDateTime,
  activatedAt: Option[LocalDateTime],
  terminatedAt: Option[LocalDateTime]
)

object WorkflowResponse {
  implicit val decoder = deriveDecoder[WorkflowResponse]
  implicit val encoder = deriveEncoder[WorkflowResponse]
}
