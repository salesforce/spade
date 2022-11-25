package com.salesforce.mce.spade.aws.activity

import com.salesforce.mce.spade.aws.spec.EmrActivitySpec

case class EmrStep(jar: String, args: String*) {

  def asSpec(): EmrActivitySpec.Step = EmrActivitySpec.Step(jar, args)

}
