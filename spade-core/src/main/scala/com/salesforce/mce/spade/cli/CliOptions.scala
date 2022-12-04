/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.cli

import com.salesforce.mce.spade.SpadeContext

case class CliOptions(
  command: Command,
  host: String,
  apiKey: Option[String],
  workflowId: String,
  pipelineName: String,
  activate: Boolean
)

object CliOptions {

  def apply(command: Command)(implicit spadeCtx: SpadeContext): CliOptions = CliOptions(
    command = command,
    apiKey = spadeCtx.apiKey,
    host = spadeCtx.orchardHost,
    workflowId = "",
    pipelineName = "",
    activate = false
  )

}
