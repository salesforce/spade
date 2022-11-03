/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.aws

import com.typesafe.config.{Config, ConfigFactory}

case class SpadeAwsContext(emr: SpadeAwsContext.Emr)

object SpadeAwsContext {

  case class Emr(
    releaseLabel: String,
    subnetId: String,
    ec2KeyName: String,
    instanceCount: Int,
    masterInstanceType: String,
    slaveInstanceType: String,
    serviceRole: String,
    resourceRole: String,
    tags: Seq[(String, String)]
  )

  object Emr {
    def configPath = "emr"

    def withRootConfig(rootConfig: Config): Emr = {
      val config = rootConfig.getConfig(configPath)

      Emr(
        config.getString("release-label"),
        config.getString("subnet-id"),
        config.getString("ec2-keyname"),
        config.getInt("instance-count"),
        config.getString("master-instance-type"),
        config.getString("slave-instance-type"),
        config.getString("service-role"),
        config.getString("resource-role"),
        Seq.empty
      )
    }

    def apply(): Emr = withRootConfig(ConfigFactory.load())
  }

  def configPath = "com.salesforce.mce.spade.aws"

  def withRootConfig(rootConfig: Config): SpadeAwsContext = {
    val config = rootConfig.getConfig(configPath)
    val emr = Emr.withRootConfig(config)
    SpadeAwsContext(emr)
  }

  def apply(): SpadeAwsContext = withRootConfig(ConfigFactory.load())

}
