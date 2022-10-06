package com.salesforce.mce.spade.examples

import com.salesforce.mce.spade.SpadeWorkflow
import com.salesforce.mce.spade.aws.SpadeAwsContext
import com.salesforce.mce.spade.aws.activity.EmrActivity
import com.salesforce.mce.spade.aws.resource.EmrCluster
import com.salesforce.mce.spade.aws.spec.EmrActivitySpec
import com.salesforce.mce.spade.cli.SpadeCli
import com.salesforce.mce.spade.workflow.WorkflowExpression

object ExampleWorkflow extends SpadeWorkflow with SpadeCli {

  implicit val spadeAwsContext = SpadeAwsContext()

  val emrCluster = EmrCluster.builder().build()

  val act1 = EmrActivity.builder(emrCluster).withSteps(EmrActivitySpec.Step("", Seq.empty)).build()
  val act2 = EmrActivity.builder(emrCluster).withSteps(EmrActivitySpec.Step("", Seq.empty)).build()

  override def workflow: WorkflowExpression = act1 ~> act2

}
