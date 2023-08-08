/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.cli

import com.salesforce.mce.spade.BuildInfo
import com.salesforce.mce.spade.SpadeWorkflowGroup
import com.salesforce.mce.telepathy.ErrorResponse
import scopt.DefaultOParserSetup
import scopt.OParser
import scopt.OParserSetup

trait SpadeCli { self: SpadeWorkflowGroup =>

  val builder = OParser.builder[CliOptions]

  /*
  This setup method allows implementations to this class to also use scopt for argument parsing.
   */
  val ignoreUnknownArguments: OParserSetup = new DefaultOParserSetup {
    override def errorOnUnknownArgument: Boolean = false
  }

  /*
  Each implementation's main method should call super.main(args) after initialization.
   */
  def main(args: Array[String]): Unit = {

    // Parse the core spadecli arguments
    val optionParseResult = parseOptions(args)

    optionParseResult match {
      // Assuming all necessary arguments were present, run the Cli command
      case Some(cliOptions: CliOptions) =>
        this.run(cliOptions)
      // If there was an error parsing necesary arguments, the program help should be printed
      case _ =>
        System.err.println("Spade Command parsing error")
    }
  }

  def parseOptions(args : Array[String]): Option[CliOptions] = {
    import builder._
    val parser = OParser.sequence(
      programName("spade-cli"),
      head("spade-cli", BuildInfo.version),
      help("help").text("prints this usage text"),
      cmd("generate")
        .action((_, c) => c.copy(command = Command.Generate))
        .children(
          opt[Unit]("compact")
            .action((_, c) => c.copy(compact = true))
            .text("If specified, the workflow json will be unindented."),
          opt[Unit]("array")
            .action((_, c) => c.copy(array = true))
            .text("If specified, the workflow json will be wrapped in array.")
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

      val parseResult = OParser.parse(parser, args, CliOptions(Command.Generate), ignoreUnknownArguments)

      parseResult
  }

  def run(opts: CliOptions) = {

    val commandResponse = opts.command match {
      case Command.Generate =>
        new GenerateCommand(opts, self).call()
      case Command.Get =>
        new GetCommand(opts).call()
      case Command.Create =>
        new CreateCommand(opts, self).call()
      case Command.Activate =>
        new ActivateCommand(opts).call()
      case Command.Delete =>
        new DeleteCommand(opts).call()
      case Command.Cancel =>
        new CancelCommand(opts).call()
    }
    commandResponse match {
      case Some(x: ErrorResponse) => x.message match {
        case Left(exception) => {
          exception.printStackTrace(System.err)
        }
        case Right(message) => {
          System.err.println(message)
        }
      } case None =>
    }

  }

}