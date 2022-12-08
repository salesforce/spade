package com.salesforce.mce.spade.orchard

import okhttp3.HttpUrl

import com.salesforce.mce.spade.SpadePipeline
import com.salesforce.mce.telepathy.{ErrorResponse, HttpRequest, TelepathySetting}

object OrchardClient {

  case class Setting(host: HttpUrl, apiKey: Option[String]) {

    implicit val telepathySetting = new TelepathySetting {
      override val headers: Map[String, String] =
        apiKey.map(k => Map("x-api-key" -> k)).getOrElse(Map.empty)
    }

  }

}

class OrchardClient(setting: OrchardClient.Setting) {

  import setting._

  def create(spadePipeline: SpadePipeline): Either[ErrorResponse, OrchardClientForPipeline] =
    HttpRequest
      .post[String, WorkflowRequest](
        host.newBuilder().addPathSegment("v1").addPathSegment("workflow").build(),
        WorkflowRequest(spadePipeline.name, spadePipeline.workflow)
      )
      .map(new OrchardClientForPipeline(setting, _))

  def forName(pipelineName: String): Either[ErrorResponse, Option[OrchardClientForPipeline]] =
    HttpRequest
      .get[List[WorkflowResponse]](
        host
          .newBuilder()
          .addPathSegments("v1")
          .addPathSegment("workflows")
          .addQueryParameter("like", pipelineName)
          .build()
      )
      .map {
        case p :: Nil =>
          Option(new OrchardClientForPipeline(setting, p.id))
        case Nil =>
          None
        case _ =>
          throw new Exception("Duplicated pipeline name detected")
      }

}
