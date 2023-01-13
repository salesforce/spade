package com.salesforce.mce.spade

import com.salesforce.mce.spade.workflow.{WorkflowExpression, WorkflowExpressionImplicits}

trait SpadeWorkflowGroup extends WorkflowExpressionImplicits {

  implicit lazy val spadeContext: SpadeContext = SpadeContext()

  def name: String = this.getClass().getCanonicalName().stripSuffix("$")

  def workflows: Map[WorkflowKey, WorkflowExpression]

  final val DefaultNameKeySeparator = "#"

  private[spade] def nameForKey(key: WorkflowKey): String =
    name + key.map(DefaultNameKeySeparator + _).getOrElse("")
}
