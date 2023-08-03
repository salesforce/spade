/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.cli

import io.circe.syntax._
import scopt.OParser

import com.salesforce.mce.spade.orchard.WorkflowRequest
import com.salesforce.mce.spade.{BuildInfo, SpadeWorkflowGroup}

trait SpadeCli { self: SpadeWorkflowGroup =>

  def main(args: Array[String]): Unit = {
    val builder = OParser.builder[CliOptions]

    val parser = {
      import builder._
      OParser.sequence(
        programName("spade-cli"),
        head("spade-cli", BuildInfo.version),
        help("help").text("prints this usage text"),
        cmd("generate")
          .action((_, c) => c.copy(command = Command.Generate))
          .children(
            opt[Unit]("compact")
              .action((_, c) => c.copy(compact = true))
              .text("If specified, the workflow json will be unindented.")
          ),
        cmd("get")
          .action((_, c) => c.copy(command = Command.Get))
          .children(
            opt[String]("name").action((x, c) => c.copy(pipelineName = x)).required()
          ),
        cmd("create")
          .action((_, c) => c.copy(command = Command.Create))
          .children(
            opt[String]('h', "host")
              .action((x, c) => c.copy(host = x))
              .text("Orchard host name"),
            opt[String]("api-key")
              .action((x, c) => c.copy(apiKey = Option(x)))
              .text("Orchard API key to use"),
            opt[Unit]("activate")
              .action((_, c) => c.copy(activate = true))
              .text("If specified, the newly created pipeline will be activated.")
          ),
        cmd("activate")
          .action((_, c) => c.copy(command = Command.Activate))
          .children(
            opt[String]('h', "host")
              .action((x, c) => c.copy(host = x))
              .text("orchard host name"),
            opt[String]("api-key")
              .action((x, c) => c.copy(apiKey = Option(x)))
              .text("Orchard API key to use"),
            opt[String]("workflow-id")
              .required()
              .action((x, c) => c.copy(workflowId = x))
              .text("Workflow ID")
          ),
        cmd("delete")
          .action((_, c) => c.copy(command = Command.Delete))
          .children(
            opt[String]('h', "host")
              .action((x, c) => c.copy(host = x))
              .text("orchard host name"),
            opt[String]("api-key")
              .action((x, c) => c.copy(apiKey = Option(x)))
              .text("Orchard API key to use"),
            opt[String]("workflow-id")
              .required()
              .action((x, c) => c.copy(workflowId = x))
              .text("Workflow ID")
          ),
        cmd("cancel")
          .action((_, c) => c.copy(command = Command.Cancel))
          .children(
            opt[String]('h', "host")
              .action((x, c) => c.copy(host = x))
              .text("orchard host name"),
            opt[String]("api-key")
              .action((x, c) => c.copy(apiKey = Option(x)))
              .text("Orchard API key to use"),
            opt[String]("workflow-id")
              .required()
              .action((x, c) => c.copy(workflowId = x))
              .text("Workflow ID")
          )
      )
    }

    OParser.parse(parser, args, CliOptions(Command.Generate)) match {
      case Some(opts) =>
        val exitStatus = opts.command match {
          case Command.Generate =>
            val requests = for {
              (workflowKey, workflow) <- self.workflows
            } yield {
              val request = WorkflowRequest(self.name, workflow)
              if (opts.compact) request.asJson.noSpaces else request.asJson.spaces2
            }
            // return json array of string blobs
            if (opts.compact) {
              println(requests.asJson.noSpaces)
            } else {
              println(requests.asJson.spaces2)
            }
            0
          case Command.Get =>
            new GetCommand(opts).run()
          case Command.Create =>
            new CreateCommand(opts, self).run()
          case Command.Activate =>
            new ActivateCommand(opts).run()
          case Command.Delete =>
            new DeleteCommand(opts).run()
          case Command.Cancel =>
            new CancelCommand(opts).run()
        }
        System.exit(exitStatus)
      case None =>
        println("error")
        System.exit(1)
    }

  }

}
