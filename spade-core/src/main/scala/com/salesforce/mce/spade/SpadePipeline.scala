/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade

import com.salesforce.mce.spade.workflow.{WorkflowExpression, WorkflowExpressionImplicits}

trait SpadePipeline extends WorkflowExpressionImplicits {

  implicit lazy val spadeContext: SpadeContext = SpadeContext()

  def name: String = this.getClass().getCanonicalName().stripSuffix("$")

  def workflow: WorkflowExpression

}
