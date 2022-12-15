package com.salesforce.mce.spade.cli

import okhttp3.HttpUrl

import com.salesforce.mce.spade.SpadePipeline
import com.salesforce.mce.spade.orchard.OrchardClient

class CreateCommand(opt: CliOptions, pipeline: SpadePipeline) {

  def run(): Int = {

    new OrchardClient(OrchardClient.Setting(HttpUrl.parse(opt.host), opt.apiKey))
      .create(pipeline)
      .flatMap { clientForPipeline =>
        println(s"created workflow: ${clientForPipeline.workflowId}")

        if (opt.activate) {
          clientForPipeline
            .activate()
            .map { _ =>
              println("workflow activated")
              0
            }
        } else {
          Right(0)
        }

      } match {
      case Left(value) =>
        println(s"Error: $value")
        1
      case Right(value) =>
        value
    }

  }

}
