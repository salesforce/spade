package com.salesforce.mce.spade.cli

import okhttp3.HttpUrl

import com.salesforce.mce.spade.orchard.OrchardClient

class GetCommand(opt: CliOptions) {

  def run(): Int = {
    new OrchardClient(OrchardClient.Setting(HttpUrl.parse(opt.host), opt.apiKey))
      .forName(opt.pipelineName) match {
        case Right(ps) =>
          ps.foreach(println)
          0
        case Left(e) =>
          println(e)
          1
      }
  }
}
