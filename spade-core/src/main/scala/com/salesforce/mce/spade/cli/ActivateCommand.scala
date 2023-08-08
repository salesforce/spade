package com.salesforce.mce.spade.cli

import java.util.concurrent.Callable

import com.salesforce.mce.spade.orchard.{OrchardClient, OrchardClientForPipeline}
import com.salesforce.mce.telepathy.ErrorResponse
import okhttp3.HttpUrl

class ActivateCommand(opt: CliOptions) extends Callable[Option[ErrorResponse]] {

  override def call(): Option[ErrorResponse] = {
    new OrchardClientForPipeline(
      OrchardClient.Setting(HttpUrl.parse(opt.host), opt.apiKey),
      opt.workflowId
    ).activate()
    None
  }

}
