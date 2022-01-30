#!/usr/bin/env kotlin

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:2.1.0")
@file:CompilerOptions("-jvm-target", "17")

import com.baulsupp.okscript.client
import com.baulsupp.okscript.flatMapMeToo
import com.baulsupp.okscript.runScript
import com.baulsupp.okurl.kotlin.postJsonBody
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.request
import java.io.File
import java.time.DayOfWeek
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneOffset

data class MeetingTime(
  val start: OffsetDateTime,
  val end: OffsetDateTime
) {
  fun before(other: MeetingTime) = end <= other.start
  fun after(other: MeetingTime) = start >= other.end
  fun overlaps(other: MeetingTime) = !(before(other) || after(other))
}

// Google Free Busy Request
data class FreeBusyRequestItem(val id: String)
data class FreeBusyRequest(
  val timeMin: Instant,
  val timeMax: Instant,
  val items: List<FreeBusyRequestItem>,
  val timeZone: String = "UTC"
)

// Google Free Busy Response
data class CalendarEntry(
  val start: Instant,
  val end: Instant
) {
  fun toMeetingTime(offset: ZoneOffset) = MeetingTime(start.atOffset(offset), end.atOffset(offset))
}

data class Calendar(val busy: List<CalendarEntry>)
data class FreeBusyResponse(
  val calendars: Map<String, Calendar>,
  val kind: String?,
  val timeMax: String?,
  val timeMin: String?
)

suspend fun busy(
  emails: List<String>,
  start_day: LocalDate,
  start_time: OffsetTime,
  end_time: OffsetTime,
  days: Long,
  offset: ZoneOffset
): Map<String, List<MeetingTime>> {
  val request = FreeBusyRequest(
    start_day.atTime(start_time)
      .toInstant(), start_day.plusDays(days)
    .atTime(end_time)
    .toInstant(), emails.map(::FreeBusyRequestItem)
  )
  val response =
    client.query<FreeBusyResponse>(request("https://www.googleapis.com/calendar/v3/freeBusy") {
      postJsonBody(request)
    })
  return response.calendars.mapValues { it.value.busy.map { it.toMeetingTime(offset) } }
}

/**
 * Get all slots during working hours starting on `start_day` and extending for `days` days. Doesn't know about holidays.
 */
fun slots(
  start_day: LocalDate,
  start_time: OffsetTime,
  end_time: OffsetTime,
  days: Long = 1,
  length: Long = 30,
  daysOfWeek: Set<DayOfWeek> = setOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)
): List<MeetingTime> {
  val times = generateSequence(start_time) {
    if (it.plusMinutes(length) < end_time) it.plusMinutes(
      length
    ) else null
  }.toList()

  return (0 until days).map { start_day.plusDays(it) }
    .filter { daysOfWeek.contains(it.dayOfWeek) }
    .flatMapMeToo { date -> times.map { time -> date.atTime(time) } }
    .map { MeetingTime(it, it.plusMinutes(length)) }
}

fun free_slots(
  slots: List<MeetingTime>,
  busy_slots: List<MeetingTime>
): List<MeetingTime> {
  // TODO implement decent version
  return slots.filter { free -> busy_slots.find { busy -> free.overlaps(busy) } == null }
}

runScript {
  val emails = File(args[0]).readLines()
//  val principal = emails.first()
  val people = emails.drop(1)

  val pacific = ZoneOffset.ofHours(-7)
  val start_day = LocalDate.now()
  val start_time = LocalTime.of(9, 0)
    .atOffset(pacific)
  val end_time = LocalTime.of(14, 0)
    .atOffset(pacific)
  val days = 30L
  val length = 30L

  val busy_times = busy(emails, start_day, start_time, end_time, days, pacific)

  val all_slots = slots(start_day, start_time, end_time, days, length)

//  val principle_slots = free_slots(all_slots, busy_times.getValue(principal))
//  val people_slots = people.keysToMap { free_slots(all_slots, busy_times.getValue(it)) }
//
//  // TODO schedule best meeting times
//  println(people_slots)
}
