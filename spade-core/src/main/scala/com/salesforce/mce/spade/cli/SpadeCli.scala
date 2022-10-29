/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.cli

import scopt.OParser
import io.circe.syntax._

import com.salesforce.mce.spade.orchard.WorkflowRequest
import com.salesforce.mce.spade.{SpadeContext, SpadeWorkflow, BuildInfo}

trait SpadeCli { self: SpadeWorkflow =>

  implicit lazy val ctx: SpadeContext = SpadeContext()

  def main(args: Array[String]): Unit = {
    val builder = OParser.builder[CliOptions]

    val parser = {
      import builder._
      OParser.sequence(
        programName("spade-cli"),
        head("spade-cli", BuildInfo.version),
        help("help").text("prints this usage text")
      )
    }

    OParser.parse(parser, args, CliOptions(Command.Generate)) match {
      case Some(opts) =>
        val request = WorkflowRequest(self.name, self.workflow)
        println(request.asJson)
      case None =>
        println("error")
    }

  }

}
