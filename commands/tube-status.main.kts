#!/usr/bin/env kotlin

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:1.0.0")

import com.baulsupp.okscript.client
import com.baulsupp.okscript.queryList
import com.baulsupp.okscript.runScript
import com.squareup.moshi.Json

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

  fun severity(): Int = lineStatuses?.map { it.statusSeverity }?.min() ?: 10
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

runScript {
  val results = client.queryList<StatusItem>(
    "https://api.tfl.gov.uk/line/mode/tube/status"
  )

  results.forEach {
    println(it.statusString())
  }
}
