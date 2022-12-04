package com.salesforce.mce.spade.cli

import okhttp3.HttpUrl

import com.salesforce.mce.spade.SpadePipeline
import com.salesforce.mce.spade.orchard.OrchardClient

class CreateCommand(opt: CliOptions, pipeline: SpadePipeline) {

  def run(): Int = {

    new OrchardClient(OrchardClient.Setting(HttpUrl.parse(opt.host), opt.apiKey))
      .create(pipeline)
      .foreach { clientForPipeline =>
        println(clientForPipeline.workflowId)

        if (opt.activate) {
          clientForPipeline.activate()
        }
      }

    0
  }

}
