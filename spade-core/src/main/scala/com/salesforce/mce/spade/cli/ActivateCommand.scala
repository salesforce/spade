package com.salesforce.mce.spade.cli

import okhttp3.HttpUrl

import com.salesforce.mce.spade.orchard.ClientForPipeline

class ActivateCommand(opt: CliOptions) {

  def run(): Int = {
    new ClientForPipeline(HttpUrl.parse(opt.host), opt.workflowId).activate()

    0
  }

}
