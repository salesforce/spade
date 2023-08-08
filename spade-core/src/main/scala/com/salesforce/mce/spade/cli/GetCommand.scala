package com.salesforce.mce.spade.cli

import java.util.concurrent.Callable

import com.salesforce.mce.spade.orchard.OrchardClient
import com.salesforce.mce.telepathy.ErrorResponse
import okhttp3.HttpUrl

class GetCommand(opt: CliOptions) extends Callable[Option[ErrorResponse]] {

  override def call(): Option[ErrorResponse] = {
    new OrchardClient(OrchardClient.Setting(HttpUrl.parse(opt.host), opt.apiKey))
      .forName(opt.pipelineName) match {
      case Right(ps) =>
        ps.foreach(println)
        None
      case Left(e) =>
        Some(e)
    }
  }
}
