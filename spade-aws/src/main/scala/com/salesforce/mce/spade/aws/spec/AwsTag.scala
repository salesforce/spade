/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.aws.spec

import io.circe._
import io.circe.generic.semiauto._

case class AwsTag(
  key: String,
  value: String
)

object AwsTag {

  implicit val decoder: Decoder[AwsTag] = deriveDecoder[AwsTag]
  implicit val encoder: Encoder[AwsTag] = deriveEncoder[AwsTag]

}
