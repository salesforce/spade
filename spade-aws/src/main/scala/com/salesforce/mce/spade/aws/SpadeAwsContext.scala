/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.aws

import com.typesafe.config.{Config, ConfigFactory}

import com.salesforce.mce.spade.SpadeContext.optionIfMissing

case class SpadeAwsContext(
  emr: SpadeAwsContext.Emr,
  ec2: SpadeAwsContext.Ec2,
  tags: Map[String, String]
)

object SpadeAwsContext {

  case class Emr(
    releaseLabel: String,
    customAmiId: Option[String],
    subnetIds: Seq[String],
    useEmrInstanceFleet: Option[Boolean],
    ec2KeyName: Option[String],
    instanceCount: Int,
    masterInstanceType: String,
    coreInstanceType: String,
    serviceRole: String,
    resourceRole: String,
    spotAllocationStrategy: Option[String],
    onDemandAllocationStrategy: Option[String],
    tags: Map[String, String]
  )

  object Emr {
    def configPath = "emr"

    def withRootConfig(rootConfig: Config): Emr = {
      val config = rootConfig.getConfig(configPath)

      Emr(
        config.getString("release-label"),
        optionIfMissing(config.getString("custom-ami-id")),
        Seq(config.getString("subnet-id")),
        optionIfMissing(config.getBoolean("use-instance-fleet")),
        optionIfMissing(config.getString("ec2-keyname")),
        config.getInt("instance-count"),
        config.getString("master-instance-type"),
        config.getString("core-instance-type"),
        config.getString("service-role"),
        config.getString("resource-role"),
        optionIfMissing(config.getString("spot-allocation-Strategy")),
        optionIfMissing(config.getString("on-demand-allocation-strategy")),
        Map.empty
      )
    }

    def apply(): Emr = withRootConfig(ConfigFactory.load())
  }

  case class Ec2(
    amiImageId: String,
    subnetId: String,
    instanceType: String,
    instanceProfile: String,
    spotInstance: Boolean,
    tags: Map[String, String]
  )

  object Ec2 {
    def configPath = "ec2"

    def withRootConfig(rootConfig: Config): Ec2 = {
      val config = rootConfig.getConfig(configPath)

      Ec2(
        config.getString("ami-image-id"),
        config.getString("subnet-id"),
        config.getString("instance-type"),
        config.getString("instance-profile"),
        config.getBoolean("spot-instance"),
        Map.empty
      )
    }

    def apply(): Ec2 = withRootConfig(ConfigFactory.load())
  }

  def configPath = "com.salesforce.mce.spade.aws"

  def withRootConfig(rootConfig: Config): SpadeAwsContext = {
    val config = rootConfig.getConfig(configPath)
    val emr = Emr.withRootConfig(config)
    val ec2 = Ec2.withRootConfig(config)
    SpadeAwsContext(emr, ec2, Map.empty)
  }

  def apply(): SpadeAwsContext = withRootConfig(ConfigFactory.load())

}
