/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.workflow

import scala.concurrent.duration._
import io.circe._

import java.time.LocalDateTime

case class Schedule(
  // run at activation by default
  nextRuntime: Option[LocalDateTime] = None,
  // run once by default
  every: Option[Duration] = None,
  backfill: Boolean = false
) {
  def once = copy(every = None)
  def every(duration: Duration) = copy(every = Some(duration))
}

object Schedule {
  def startAt(datetime: LocalDateTime): Schedule = {
    if (datetime.isBefore(java.time.LocalDateTime.now())) {
      Schedule(nextRuntime = Option(datetime), backfill = true)
    } else {
      Schedule(nextRuntime = Option(datetime))
    }
  }

  def startAt(datetime: String): Schedule = {
    startAt(LocalDateTime.parse(datetime))
  }

  def startAtActivation(): Schedule = Schedule()

  implicit val encodeSchedule: Encoder[Schedule] = {
    Encoder.forProduct3("nextRuntime", "every", "backfill")(schedule => {
      val encodeEvery = schedule.every match {
        case Some(duration) => Some(duration.toString)
        case None => None
      }
      (schedule.nextRuntime, encodeEvery, schedule.backfill)
    })
  }
}
