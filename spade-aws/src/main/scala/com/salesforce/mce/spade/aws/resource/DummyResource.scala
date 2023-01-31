package com.salesforce.mce.spade.aws.resource

import java.util.UUID
import io.circe.syntax._

import com.salesforce.mce.spade.SpadeContext
import com.salesforce.mce.spade.aws.spec.DummyResourceSpec
import com.salesforce.mce.spade.workflow.Resource

trait DummyResource

object DummyResource {

  final val ResourceType = "v.DummyResource"

  case class Builder(
    nameOpt: Option[String],
    initSecondsOpt: Option[Int]
  ) {

    def withName(name: String) = copy(nameOpt = Option(name))

    def withInitSeconds(seconds: Int) = copy(initSecondsOpt = Option(seconds))

    def build()(implicit ctx: SpadeContext): Resource[DummyResource] = {

      val id = UUID.randomUUID().toString()
      val name = nameOpt.getOrElse(s"DummyResource-$id")

      Resource[DummyResource](
        id,
        name,
        ResourceType,
        DummyResourceSpec(
          initSecondsOpt.getOrElse(10)
        ).asJson,
        ctx.maxAttempt,
        None
      )
    }
  }

  def builder(): DummyResource.Builder =
    Builder(None, None)

}
