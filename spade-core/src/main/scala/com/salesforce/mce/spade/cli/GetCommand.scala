package com.salesforce.mce.spade.cli

import com.salesforce.mce.spade.orchard.OrchardClient
import okhttp3.HttpUrl

class GetCommand(opt: CliOptions) {

  def run(): Int = {
    new OrchardClient(OrchardClient.Setting(HttpUrl.parse(opt.host), opt.apiKey))
      .forName(opt.pipelineName) match {
        case Right(p) =>
          println(p)
          0
        case Left(e) =>
          println(e)
          1
      }
  }
}
