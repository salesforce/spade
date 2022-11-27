package com.salesforce.mce.spade.cli

import com.salesforce.mce.telepathy.TelepathySetting
import com.salesforce.mce.telepathy.HttpRequest
import okhttp3.HttpUrl

class ActivateCommand(opt: CliOptions) {

  def run(): Int = {
    implicit val telepathySetting = TelepathySetting()

    HttpRequest.put[String, Option[String]](
      HttpUrl.parse(s"${opt.host}/v1/workflow/${opt.pipelineId}"),
      None
    )

    0
  }

}
