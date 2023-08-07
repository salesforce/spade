package com.salesforce.mce.spade.cli

import com.salesforce.mce.spade.orchard.WorkflowRequest
import com.salesforce.mce.spade.SpadeWorkflowGroup
import com.salesforce.mce.telepathy.ErrorResponse
import io.circe.syntax._

import java.util.concurrent.Callable

class GenerateCommand(opt: CliOptions, workflowGroup: SpadeWorkflowGroup) extends Callable[Option[ErrorResponse]] {

  override def call(): Option[ErrorResponse] = {
    val requests = for {
      (workflowKey, workflow) <- workflowGroup.workflows
    } yield {
      WorkflowRequest(workflowGroup.name, workflow).asJson
    }

    (opt.array, opt.compact) match {
      case (true, true) =>
        println(requests.map(_.noSpaces).asJson.noSpaces)
      case (true, false) =>
        println(requests.map(_.spaces2).asJson.spaces2)
      case (false, true) =>
        requests.foreach(r => println(r.noSpaces))
      case (false, false) =>
        requests.foreach(r => println(r.spaces2))
    }
    None
  }


}
