/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.cli

trait Command

/*
The are the core commands supported by SpadeCli.
 */
object Command {

  case object Generate extends Command

  case object Create extends Command

  case object Activate extends Command

  case object Delete extends Command

  case object Get extends Command

  case object Cancel extends Command

}
