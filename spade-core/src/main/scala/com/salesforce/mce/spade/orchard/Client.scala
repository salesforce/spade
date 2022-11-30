package com.salesforce.mce.spade.orchard

import okhttp3.HttpUrl

import com.salesforce.mce.spade.SpadePipeline
import com.salesforce.mce.telepathy.{ErrorResponse, HttpRequest, TelepathySetting}

class Client(orchardHost: HttpUrl) {

  implicit val telepathySetting = TelepathySetting()

  def create(spadePipeline: SpadePipeline): Either[ErrorResponse, ClientForPipeline] =
    HttpRequest
      .post[String, WorkflowRequest](
        orchardHost.newBuilder().addPathSegment("v1").addPathSegment("workflow").build(),
        WorkflowRequest(spadePipeline.name, spadePipeline.workflow)
      )
      .map(new ClientForPipeline(orchardHost, _))

}

class ClientForPipeline(orchardHost: HttpUrl, val workflowId: String) {

  implicit val telepathySetting = TelepathySetting()

  def activate(): Either[ErrorResponse, ClientForPipeline] =
    HttpRequest
      .put[String, Option[Int]](
        orchardHost
          .newBuilder()
          .addPathSegment("v1")
          .addPathSegment("workflow")
          .addPathSegment(workflowId)
          .build(),
        None
      )
      .map(_ => this)

}
