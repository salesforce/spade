/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.workflow

import io.circe.Json

case class Activity[R](
  id: String,
  name: String,
  activityType: String,
  activitySpec: Json,
  runsOn: Resource[R],
  maxAttempt: Int,
  onSuccess: Option[Seq[Action]],
  onFail: Option[Seq[Action]]
)
