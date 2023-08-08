package com.salesforce.mce.spade.cli

import java.util.concurrent.Callable

import com.salesforce.mce.spade.SpadeWorkflowGroup
import com.salesforce.mce.spade.orchard.OrchardClient
import com.salesforce.mce.spade.orchard.OrchardClientForPipeline
import com.salesforce.mce.telepathy.ErrorResponse
import okhttp3.HttpUrl

class CreateCommand(opt: CliOptions, workflowGroup: SpadeWorkflowGroup) extends Callable[Option[ErrorResponse]] {

  override def call(): Option[ErrorResponse] = {

    new OrchardClient(OrchardClient.Setting(HttpUrl.parse(opt.host), opt.apiKey))
      .create(workflowGroup)
      .fold (
        error => {
          println(s"Creation Error: ${error._1}")
          error._2.foreach(_.delete())
          Some(error._1)
        },
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
                Some(error)
              case _ =>
                None
            }
          } else {
            None
          }
      )
  }

}
