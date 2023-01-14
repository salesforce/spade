package com.salesforce.mce.spade.orchard

import okhttp3.HttpUrl

import com.salesforce.mce.spade.SpadeWorkflowGroup
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

  def create(spadeWorkflowGroup: SpadeWorkflowGroup) = {
    val created = spadeWorkflowGroup.workflows.toSeq
      .foldLeft(
        (Option.empty[ErrorResponse], Seq.empty[OrchardClientForPipeline])
      ) { case ((error, succeeded), (wfKey, wf)) =>
        error
          .fold(
            HttpRequest
              .post[String, WorkflowRequest](
                host.newBuilder().addPathSegment("v1").addPathSegment("workflow").build(),
                WorkflowRequest(spadeWorkflowGroup.nameForKey(wfKey), wf)
              )
              .map { workflowId =>
                println(s"created workflow: $workflowId")
                new OrchardClientForPipeline(setting, workflowId)
              }
              .fold(e => (Option(e), succeeded), fb => (None, fb +: succeeded))
          )(_ =>
            (error, succeeded)
          )
      }
    created match {
      case (Some(error), succeeded) =>
        Left((error, succeeded))
      case (None, succeeded) =>
        Right(succeeded)
    }
  }

  def forName(pipelineName: String): Either[ErrorResponse, Seq[OrchardClientForPipeline]] =
    HttpRequest
      .get[List[WorkflowResponse]](
        host
          .newBuilder()
          .addPathSegments("v1")
          .addPathSegment("workflows")
          .addQueryParameter("like", pipelineName)
          .build()
      )
      .map { ps =>
        ps.map(p => new OrchardClientForPipeline(setting, p.id))
      }

}
