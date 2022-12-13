package com.salesforce.mce.spade.examples

import com.salesforce.mce.spade.SpadePipeline
import com.salesforce.mce.spade.aws.SpadeAwsContext
import com.salesforce.mce.spade.aws.activity.{EmrActivity, EmrStep, ShellScriptActivity}
import com.salesforce.mce.spade.aws.resource.{Ec2Cluster, EmrCluster}
import com.salesforce.mce.spade.cli.SpadeCli
import com.salesforce.mce.spade.workflow.WorkflowExpression

object ExampleWorkflow extends SpadePipeline with SpadeCli {

  implicit val spadeAwsContext = SpadeAwsContext()

  val emrCluster = EmrCluster.builder().build()

  val act1 = EmrActivity.builder(emrCluster).withSteps(EmrStep("s3://somebucket/some.jar")).build()
  val act2 = EmrActivity.builder(emrCluster).withSteps(EmrStep("s3://somebucket/some.jar")).build()

  val ec2Cluster = Ec2Cluster.builder().build()
  val act3 = ShellScriptActivity.builder(ec2Cluster, "s3://somebucket/script.sh", "somebucket", "output").build()

  override def workflow: WorkflowExpression = act1 ~> act2 ~> act3

}
