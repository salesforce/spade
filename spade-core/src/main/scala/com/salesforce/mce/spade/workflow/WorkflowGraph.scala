package com.salesforce.mce.spade.workflow

class WorkflowGraph(
  val activities: Map[String, Activity[_]],
  // value depends on key, run key and then run value
  val flows: Set[(String, String)]
) {

  private val lefts = flows.map(_._1)
  private val rights = flows.map(_._2)
  private val allActivities = activities.keySet

  val startingActivites = allActivities -- rights

  val endingActivites = allActivities -- lefts

  def ~>(right: WorkflowGraph): WorkflowGraph = {
    val newActivities = activities ++ right.activities

    // Note this does not remove transitive dependencies
    val newFlows = flows ++ right.flows ++ (
      for {
        l <- endingActivites
        r <- right.startingActivites
      } yield l -> r
    )
    new WorkflowGraph(newActivities, newFlows)
  }

  def ++(right: WorkflowGraph): WorkflowGraph = {
    val newActivities = activities ++ right.activities
    val newFlows = flows ++ right.flows
    new WorkflowGraph(newActivities, newFlows)
  }

}

object WorkflowGraph {

  def apply(): WorkflowGraph = new WorkflowGraph(Map.empty, Set.empty)

  def apply(act: Activity[_]): WorkflowGraph =
    new WorkflowGraph(Map(act.id -> act), Set.empty)

  def apply(exp: WorkflowExpression): WorkflowGraph = exp match {
    case WorkflowNoActivityExpression => WorkflowGraph()
    case WorkflowActivityExpression(act) => WorkflowGraph(act)
    case WorkflowArrowExpression(left, right) => WorkflowGraph(left) ~> WorkflowGraph(right)
    case WorkflowPlusExpression(left, right) => WorkflowGraph(left) ++ WorkflowGraph(right)
  }
}
