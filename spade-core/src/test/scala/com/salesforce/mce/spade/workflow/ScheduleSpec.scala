/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.mce.spade.workflow

import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._
import io.circe._
import io.circe.syntax._

import java.time.LocalDateTime

class ScheduleSpec extends AnyWordSpec {
  "startAt" should {
    "work with LocalDateTime object" in {
      val nextYear = java.time.LocalDateTime.now.getYear + 1
      val schedule = Schedule.startAt(LocalDateTime.of(nextYear, 1, 1, 0, 30, 22))
      assertResult(Some(LocalDateTime.of(nextYear, 1, 1, 0, 30, 22))) { schedule.nextRuntime }
      assertResult(false) { schedule.backfill }
    }

    "work with String time object" in {
      val schedule = Schedule.startAt("2023-02-22T12:00:00")
      assertResult(Some(LocalDateTime.of(2023, 2, 22, 12, 0, 0))) { schedule.nextRuntime }
      assertResult(true) { schedule.backfill }
    }
  }

  "startAtActivation" should {
    "not set a value for nextRuntime attribute" in {
      val schedule = Schedule.startAtActivation()
      assertResult(None) { schedule.nextRuntime }
      assertResult(false) { schedule.backfill }
    }
  }

  "once" should {
    "not set a value for every attribute" in {
      val schedule = Schedule.startAtActivation().once
      assertResult(None) { schedule.every }
    }
  }

  "every" should {
    "set a duration for every attribute" in {
      val schedule = Schedule.startAtActivation().every(2.days)
      assertResult(Some(2.days)) { schedule.every }
    }
  }

  "asJson" should {
    "render correct json object when all values are set" in {
      val schedule = Schedule.startAt(LocalDateTime.of(2022, 1, 1, 0, 30, 22)).every(3.hours)
      val json = """{
        "nextRuntime" : "2022-01-01T00:30:22",
        "every" : "3 hours",
        "backfill" : true
      }"""

      assertResult(parser.decode[Json](json).getOrElse(0)) { schedule.asJson }
    }

    "render correct json object with unset values" in {
      val schedule = Schedule.startAtActivation.once
      val json =
        """{
        "nextRuntime" : null,
        "every" : null,
        "backfill" : false
      }"""
      assertResult(parser.decode[Json](json).getOrElse(0)) { schedule.asJson }
    }
  }
}
