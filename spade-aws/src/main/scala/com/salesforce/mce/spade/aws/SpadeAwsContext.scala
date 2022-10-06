package com.salesforce.mce.spade.aws

import com.typesafe.config.{Config, ConfigFactory}

class SpadeAwsContext private (config: Config) {

  val emr: SpadeAwsContext.Emr = SpadeAwsContext.Emr.withRootConfig(config)

}

object SpadeAwsContext {

  class Emr private (config: Config) {

    def releaseLabel: String = config.getString("release-label")

    def subnetId: String = config.getString("subnet-id")

    def ec2KeyName: String = config.getString("ec2-keyname")

    def instanceCount: Int = config.getInt("instance-count")

    def masterInstanceType: String = config.getString("master-instance-type")

    def slaveInstanceType: String = config.getString("slave-instance-type")

    def serviceRole: String = config.getString("service-role")

    def resourceRole: String = config.getString("resource-role")

    def tags: Seq[(String, String)] = Seq.empty

  }

  object Emr {
    def configPath = "emr"

    def withRootConfig(rootConfig: Config): Emr =
      new Emr(rootConfig.getConfig(configPath))

    def apply(): Emr = withRootConfig(ConfigFactory.load())
  }

  def configPath = "com.salesforce.mce.spade.aws"

  def withRootConfig(rootConfig: Config): SpadeAwsContext =
    new SpadeAwsContext(rootConfig.getConfig(configPath))

  def apply(): SpadeAwsContext = withRootConfig(ConfigFactory.load())

}
