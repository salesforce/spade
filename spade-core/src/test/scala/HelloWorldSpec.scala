/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade

import org.scalatest.wordspec.AnyWordSpec

class HelloWorldSpec extends AnyWordSpec {
  "Hello World" when {
    "It runs" should {
      "do stuff" in {
        assert(1 + 1 === 2)
      }
    }
  }
}
