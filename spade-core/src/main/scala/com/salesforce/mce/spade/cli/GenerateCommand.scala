package com.salesforce.mce.spade.cli

import io.circe.syntax._

import com.salesforce.mce.spade.orchard.WorkflowRequest
import com.salesforce.mce.spade.SpadeWorkflowGroup

class GenerateCommand(opt: CliOptions, workflowGroup: SpadeWorkflowGroup) {

  def run(): Int = {
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
    0
  }

}
