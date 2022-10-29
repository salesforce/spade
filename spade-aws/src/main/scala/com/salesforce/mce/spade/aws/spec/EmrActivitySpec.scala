/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.aws.spec

import io.circe._
import io.circe.generic.semiauto._

case class EmrActivitySpec(
  steps: Seq[EmrActivitySpec.Step]
)

object EmrActivitySpec {

  case class Step(
    jar: String,
    args: Seq[String]
  )

  implicit val stepDecoder: Decoder[Step] = deriveDecoder[Step]
  implicit val stepEncoder: Encoder[Step] = deriveEncoder[Step]

  implicit val decoder: Decoder[EmrActivitySpec] = deriveDecoder[EmrActivitySpec]
  implicit val encoder: Encoder[EmrActivitySpec] = deriveEncoder[EmrActivitySpec]

}
