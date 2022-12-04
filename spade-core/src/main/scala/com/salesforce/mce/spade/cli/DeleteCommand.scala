package com.salesforce.mce.spade.cli

import com.salesforce.mce.spade.orchard.OrchardClientForPipeline
import com.salesforce.mce.spade.orchard.OrchardClient
import okhttp3.HttpUrl

class DeleteCommand(opt: CliOptions) {

  def run(): Int = {
    new OrchardClientForPipeline(
      OrchardClient.Setting(HttpUrl.parse(opt.host), opt.apiKey),
      opt.workflowId
    ).delete() match {
      case Right(i) =>
        println(i)
        0
      case Left(e) =>
        println(e)
        1
    }
  }

}
