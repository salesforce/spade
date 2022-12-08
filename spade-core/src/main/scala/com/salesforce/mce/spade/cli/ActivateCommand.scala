package com.salesforce.mce.spade.cli

import okhttp3.HttpUrl

import com.salesforce.mce.spade.orchard.{OrchardClient, OrchardClientForPipeline}

class ActivateCommand(opt: CliOptions) {

  def run(): Int = {
    new OrchardClientForPipeline(
      OrchardClient.Setting(HttpUrl.parse(opt.host), opt.apiKey),
      opt.workflowId
    ).activate()

    0
  }

}
