/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade

import com.typesafe.config.{Config, ConfigFactory}

case class SpadeContext(maxAttempt: Int)

object SpadeContext {

  val configPath = "com.salesforce.mce.spade"

  def withRootConfig(rootConfig: Config): SpadeContext = {
    val config = rootConfig.getConfig(configPath)
    SpadeContext(
      config.getInt("max-attempt")
    )
  }

  def apply(): SpadeContext = withRootConfig(ConfigFactory.load())

}
