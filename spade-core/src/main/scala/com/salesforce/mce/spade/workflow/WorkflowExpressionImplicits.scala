/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.workflow

import scala.language.implicitConversions

trait WorkflowExpressionImplicits {

  implicit def activity2WorkflowExpression(act: Activity[_]): WorkflowExpression =
    WorkflowActivityExpression(act)

}
