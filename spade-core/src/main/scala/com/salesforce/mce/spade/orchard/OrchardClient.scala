package com.salesforce.mce.spade.orchard

import scala.collection.compat.immutable.LazyList

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

  type Output =
    Either[(ErrorResponse, Seq[OrchardClientForPipeline]), Seq[OrchardClientForPipeline]]

  def create(spadeWorkflowGroup: SpadeWorkflowGroup) = spadeWorkflowGroup.workflows.toSeq
    .to(LazyList)
    .map { case (wfKey, wf) =>
      HttpRequest
        .post[String, WorkflowRequest](
          host.newBuilder().addPathSegment("v1").addPathSegment("workflow").build(),
          WorkflowRequest(spadeWorkflowGroup.nameForKey(wfKey), wf)
        )
        .map { workflowId =>
          new OrchardClientForPipeline(setting, workflowId)
        }
    }
    .lazyFold[Output](Right(Seq.empty)) {
      case (Right(xs), Right(x)) =>
        Right(xs :+ x) -> true
      case (Right(xs), Left(e)) =>
        Left((e, xs)) -> false
      case other =>
        throw new MatchError(other.toString())
    }

  def forName(pipelineName: String): Either[ErrorResponse, Seq[OrchardClientForPipeline]] =
    HttpRequest
      .get[List[WorkflowResponse]](
        host
          .newBuilder()
          .addPathSegments("v1")
          .addPathSegment("workflows")
          .addQueryParameter("like", s"${pipelineName}#%")
          .build()
      )
      .map { ps =>
        ps.map(p => new OrchardClientForPipeline(setting, p.id))
      }

}
