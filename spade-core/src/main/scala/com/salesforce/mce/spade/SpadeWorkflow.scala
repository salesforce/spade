package com.salesforce.mce.spade

import com.salesforce.mce.spade.workflow.{WorkflowExpression, WorkflowExpressionImplicits}

trait SpadeWorkflow extends WorkflowExpressionImplicits {

  def name: String = this.getClass().getCanonicalName().stripSuffix("$")

  def workflow: WorkflowExpression

}
