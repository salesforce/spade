package com.salesforce.mce.spade.workflow

import scala.language.implicitConversions

trait WorkflowExpressionImplicits {

  implicit def activity2WorkflowExpression(act: Activity[_]): WorkflowExpression =
    WorkflowActivityExpression(act)

}
