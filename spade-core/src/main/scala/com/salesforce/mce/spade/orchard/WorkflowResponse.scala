package com.salesforce.mce.spade.orchard

import io.circe.generic.semiauto._
import java.time.LocalDateTime

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
