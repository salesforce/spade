package com.salesforce.mce.spade.aws.resource

import com.salesforce.mce.spade.aws.spec.EmrResourceSpec
import com.salesforce.mce.spade.aws.util._

case class EmrConfiguration private (
  classification: String,
  properties: Map[String, String],
  configurations: Seq[EmrConfiguration]
) {

  def withProperty(k: String, v: String) = copy(properties = properties + (k -> v))

  def withConfiguration(c: EmrConfiguration) = copy(configurations = configurations :+ c)

  def asSpec(): EmrResourceSpec.Configuration = EmrResourceSpec.Configuration(
    classification,
    properties.asOption(),
    configurations.map(_.asSpec()).asOption()
  )

}

object EmrConfiguration {

  def apply(classification: String): EmrConfiguration =
    EmrConfiguration(classification, Map.empty, Seq.empty)

}
