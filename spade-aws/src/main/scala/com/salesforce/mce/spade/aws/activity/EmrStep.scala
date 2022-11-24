package com.salesforce.mce.spade.aws.activity

import com.salesforce.mce.spade.aws.spec.EmrActivitySpec

case class EmrStep(jar: String, args: Seq[String]) {

  def asSpec(): EmrActivitySpec.Step = EmrActivitySpec.Step(jar, args)

}
