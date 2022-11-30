package com.salesforce.mce.spade.cli

import okhttp3.HttpUrl

import com.salesforce.mce.spade.SpadePipeline
import com.salesforce.mce.spade.orchard.Client

class CreateCommand(opt: CliOptions, pipeline: SpadePipeline) {

  def run(): Int = {

    new Client(HttpUrl.parse(opt.host)).create(pipeline).foreach { clientForPipeline =>
      println(clientForPipeline.workflowId)

      if (opt.activate) {
        clientForPipeline.activate()
      }
    }

    0
  }

}
