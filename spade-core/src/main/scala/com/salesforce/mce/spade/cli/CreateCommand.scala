package com.salesforce.mce.spade.cli

import okhttp3.HttpUrl

import com.salesforce.mce.spade.SpadeWorkflowGroup
import com.salesforce.mce.spade.orchard.{OrchardClient, OrchardClientForPipeline}
import com.salesforce.mce.telepathy.ErrorResponse

class CreateCommand(opt: CliOptions, workflowGroup: SpadeWorkflowGroup) {

  def run(): Int = {

    new OrchardClient(OrchardClient.Setting(HttpUrl.parse(opt.host), opt.apiKey))
      .create(workflowGroup)
      .fold (
        error => {
          println(s"Creation Error: ${error._1}")
          error._2.foreach(_.delete())
          1
        }
        ,
        createdWorkflows =>
          if (opt.activate) {
            val activation = createdWorkflows
              .foldLeft(
                (Option.empty[ErrorResponse], Seq.empty[OrchardClientForPipeline])
              ) { case ((error, succeeded), wf) =>
                println(s"created workflow: ${wf.workflowId}")
                error
                  .fold(
                    wf
                      .activate()
                      .fold(
                        e => (Option(e), succeeded),
                        fb => {
                          println(s"workflow ${fb.workflowId} activated")
                          (None, fb +: succeeded)
                        }
                      )
                  )(_ =>
                    (error, succeeded)
                  )
              }
            activation match {
              case (Some(error), activated) =>
                println(s"Activation Error: $error")
                activated.foreach(_.cancel())
                1
              case _ =>
                0
            }
          } else {
            0
          }
      )
  }

}
