#!/usr/bin/env kotlin

import com.baulsupp.okurl.kotlin.client
import com.baulsupp.okurl.kotlin.queryList
import com.squareup.moshi.Json
import kotlinx.coroutines.runBlocking

data class StatusItem(
  val modeName: String,
  val lineStatuses: List<LineStatusesItem>?,
  val crowding: Crowding,
  val created: String,
  val name: String,
  val modified: String,
  val serviceTypes: List<ServiceTypesItem>?,
  val id: String,
  @Json(name = "\$type") val type: String
) {
  fun statusString(): String {
    return "%s".format(this.lineStatuses?.sortedBy { it.statusSeverity }?.firstOrNull()?.statusSeverityDescription)
  }

  fun severity(): Int = lineStatuses?.map { it.statusSeverity }?.minOrNull() ?: 10
}

data class LineStatusesItem(
  val statusSeverityDescription: String,
  val created: String,
  val statusSeverity: Int,
  val id: Int,
  @Json(name = "\$type") val type: String?
)

data class Crowding(@Json(name = "\$type") val type: String)

data class ServiceTypesItem(val name: String, val uri: String, @Json(name = "\$type") val type: String?)

suspend fun queryStatus() = client.queryList<StatusItem>(
  "https://api.tfl.gov.uk/line/mode/tube/status"
)

runBlocking {
  val results = queryStatus()

  results.forEach {
    println(it.statusString())
  }
}
