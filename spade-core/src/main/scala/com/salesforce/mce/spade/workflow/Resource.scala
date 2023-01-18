/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.workflow

import java.time.Duration

import io.circe.Json

case class Resource[+R](
  id: String,
  name: String,
  resourceType: String,
  resourceSpec: Json,
  maxAttempt: Int,
  terminateAfter: Option[Duration]
)
