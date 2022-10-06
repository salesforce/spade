package com.salesforce.mce.spade.workflow

sealed abstract class WorkflowExpression {

  def andThen(right: WorkflowExpression): WorkflowExpression = (this, right) match {
    case (l, WorkflowNoActivityExpression) => l
    case (WorkflowNoActivityExpression, r) => r
    case (l, r) => WorkflowArrowExpression(l, r)
  }

  def ~>(right: WorkflowExpression): WorkflowExpression = andThen(right)

  def and(right: WorkflowExpression): WorkflowExpression = (this, right) match {
    case (l, WorkflowNoActivityExpression) => l
    case (WorkflowNoActivityExpression, r) => r
    case (l, r) => WorkflowPlusExpression(l, r)
  }

  def +(right: WorkflowExpression): WorkflowExpression = and(right)

}

object WorkflowExpression extends WorkflowExpressionImplicits

case object WorkflowNoActivityExpression extends WorkflowExpression

case class WorkflowActivityExpression(act: Activity[_]) extends WorkflowExpression

case class WorkflowArrowExpression(left: WorkflowExpression, right: WorkflowExpression)
    extends WorkflowExpression

case class WorkflowPlusExpression(left: WorkflowExpression, right: WorkflowExpression)
    extends WorkflowExpression
