/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade

import com.typesafe.config.{Config, ConfigException, ConfigFactory}

case class SpadeContext(maxAttempt: Int, orchardHost: String, apiKey: Option[String])

object SpadeContext {

  def optionIfMissing[A](result: =>A): Option[A] = try {
    Option(result)
  } catch {
    case e: ConfigException.Missing => None
  }

  val configPath = "com.salesforce.mce.spade"

  def withRootConfig(rootConfig: Config): SpadeContext = {
    val config = rootConfig.getConfig(configPath)
    SpadeContext(
      config.getInt("max-attempt"),
      config.getString("orchard.host"),
      optionIfMissing(config.getString("orchard.api-key"))
    )
  }

  def apply(): SpadeContext = withRootConfig(ConfigFactory.load())

}
