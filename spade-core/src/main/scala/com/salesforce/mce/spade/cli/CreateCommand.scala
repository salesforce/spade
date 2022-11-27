package com.salesforce.mce.spade.cli

import okhttp3.HttpUrl

import com.salesforce.mce.spade.SpadeWorkflow
import com.salesforce.mce.spade.orchard.WorkflowRequest
import com.salesforce.mce.telepathy.{HttpRequest, TelepathySetting}

class CreateCommand(opt: CliOptions, workflow: SpadeWorkflow) {

  def run(): Int = {
    implicit val telepathySetting = TelepathySetting()

    val workflowId = HttpRequest.post[String, WorkflowRequest](
      HttpUrl.parse(s"${opt.host}/v1/workflow"),
      WorkflowRequest(workflow.name, workflow.workflow)
    )

    println(workflowId)

    0
  }

}
