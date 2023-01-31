package com.salesforce.mce.spade.examples

import com.salesforce.mce.spade.SpadeWorkflow
import com.salesforce.mce.spade.aws.SpadeAwsContext
import com.salesforce.mce.spade.aws.action.SnsAlarm
import com.salesforce.mce.spade.aws.activity.{
  DummyActivity,
  EmrActivity,
  EmrStep,
  ShellCommandActivity,
  ShellScriptActivity
}
import com.salesforce.mce.spade.aws.resource.{
  DummyResource,
  Ec2Instance,
  EmrCluster
}
import com.salesforce.mce.spade.cli.SpadeCli
import com.salesforce.mce.spade.workflow.WorkflowExpression

object ExampleWorkflow extends SpadeWorkflow with SpadeCli {

  implicit val spadeAwsContext = SpadeAwsContext()

  val emrCluster = EmrCluster.builder().build()

  val alarm1 = SnsAlarm("topic", "name").withSubject("Success Alarm").withMessage("success")
  val alarm2 = SnsAlarm("topic", "name").withSubject("Failure Alarm").withMessage("failed")

  val act1 = EmrActivity.builder(emrCluster).withSteps(EmrStep("s3://somebucket/some.jar")).onFail(alarm2).build()
  val act2 = EmrActivity.builder(emrCluster).withSteps(EmrStep("s3://somebucket/some.jar")).onSuccess(alarm1).build()

  val ec2Instance = Ec2Instance.builder().build()
  val act3 = ShellScriptActivity.builder(ec2Instance, "s3://somebucket/script.sh").onFail(alarm2).build()
  val act4 = ShellCommandActivity.builder(ec2Instance).withLines("hello").onSuccess(alarm1).build()

  val dummyResource = DummyResource.builder().build()
  val dummyAct = DummyActivity.builder(dummyResource).withSleepSeconds(30).onFail(alarm2).build()

  override def workflow: WorkflowExpression = act1 ~> act2 ~> act3 ~> act4 ~> dummyAct

}
