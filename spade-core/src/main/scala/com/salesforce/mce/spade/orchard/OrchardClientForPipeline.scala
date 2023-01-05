package com.salesforce.mce.spade.orchard

import com.salesforce.mce.telepathy.{ErrorResponse, HttpRequest}

class OrchardClientForPipeline(setting: OrchardClient.Setting, val workflowId: String) {

  import setting._

  def activate(): Either[ErrorResponse, OrchardClientForPipeline] =
    HttpRequest
      .put[String, Option[Int]](
        host
          .newBuilder()
          .addPathSegment("v1")
          .addPathSegment("workflow")
          .addPathSegment(workflowId)
          .addPathSegment("activate")
          .build(),
        None
      )
      .map(_ => this)

  def delete(): Either[ErrorResponse, OrchardClient] =
    HttpRequest
      .delete[Int](
        host
          .newBuilder()
          .addPathSegment("v1")
          .addPathSegment("workflow")
          .addPathSegment(workflowId)
          .build()
      )
      .map(_ => new OrchardClient(setting))

  def cancel(): Either[ErrorResponse, OrchardClientForPipeline] =
    HttpRequest
      .put[String, Option[Int]](
        host
          .newBuilder()
          .addPathSegment("v1")
          .addPathSegment("workflow")
          .addPathSegment(workflowId)
          .addPathSegment("cancel")
          .build(),
        None
      )
      .map(_ => this)
}
