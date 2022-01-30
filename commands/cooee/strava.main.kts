#!/usr/bin/env kotlin

@file:Repository("https://jcenter.bintray.com")
@file:Repository("https://jitpack.io")
@file:Repository("https://repo1.maven.org/maven2/")
@file:Repository("https://oss.jfrog.org/oss-snapshot-local")
@file:DependsOn("io.rsocket.kotlin:rsocket-core-jvm:0.13.0-SNAPSHOT")
@file:DependsOn("io.rsocket.kotlin:rsocket-transport-ktor-client-jvm:0.13.0-SNAPSHOT")
@file:DependsOn("io.ktor:ktor-client-okhttp:1.5.2")
@file:DependsOn("com.github.yschimke:cooee-cli:0.7")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
@file:DependsOn("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
@file:DependsOn("com.squareup.wire:wire-runtime:3.4.0")
@file:DependsOn("com.squareup.wire:wire-moshi-adapter:3.4.0")
@file:DependsOn("com.github.yschimke:okurl:3.2")
@file:DependsOn("com.github.yschimke:okurl-script:2.1.0")
@file:CompilerOptions("-jvm-target", "17")

import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CommandStatus
import com.baulsupp.cooee.p.ImageUrl
import com.baulsupp.cooee.sdk.SimpleServer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import com.baulsupp.okscript.client
import com.baulsupp.okscript.query
import com.baulsupp.okscript.queryList
import com.baulsupp.okurl.services.mapbox.staticMap
import com.baulsupp.okurl.services.strava.model.ActivitySummary

val server = SimpleServer(debug = false, local = false)

fun Double.format(digits: Int): String = java.lang.String.format("%.${digits}f", this)

suspend fun lastRun(): CommandResponse {
  val activities =
    client.queryList<ActivitySummary>(
      "https://www.strava.com/api/v3/athlete/activities?page=1&per_page=1"
    )

  if (activities.isEmpty()) {
    return CommandResponse(message = "No Activities Found", status = CommandStatus.REQUEST_ERROR)
  }

  val lastActivity =
    client.query<ActivitySummary>(
      "https://www.strava.com/api/v3/activities/${activities.first().id}"
    )

  val map = lastActivity.map?.let {
    staticMap {
      route(it.polyline)
    }
  }
  val url = "https://www.strava.com/activities/${lastActivity.id}"
  val distance = "Distance: ${(lastActivity.distance / 1000.0).format(1)} km"
  val duration = "Duration: ${(lastActivity.elapsed_time / 60.0).format(0)} minutes"

  return CommandResponse(message = "$distance\n$duration", image_url = map?.let { ImageUrl(it) }, url = url, status = CommandStatus.DONE)
}

server.exportSimpleCommands("strava", completer = {
  listOf("open", "lastrun")
}) {
  when (it.parsed_command.joinToString(" ")) {
    "strava", "strava open" -> flowOf(CommandResponse("https://strava.com", status = CommandStatus.REDIRECT))
    "strava lastrun" -> flow { emit(lastRun()) }
    else -> flowOf(CommandResponse(message = "Unknown strava command " + it.parsed_command, status = CommandStatus.REQUEST_ERROR))
  }
}
