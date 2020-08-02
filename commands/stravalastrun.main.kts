#!/usr/bin/env kotlin

@file:Repository("https://jitpack.io/")
@file:DependsOn("com.github.yschimke:okurl-script:1.0.0")

import com.baulsupp.okscript.client
import com.baulsupp.okscript.query
import com.baulsupp.okscript.queryList
import com.baulsupp.okscript.runScript
import com.baulsupp.okscript.show
import com.baulsupp.oksocial.output.UsageException
import com.baulsupp.okurl.services.mapbox.StaticMapBuilder
import com.baulsupp.okurl.services.mapbox.staticMap
import com.baulsupp.okurl.services.strava.model.ActivitySummary

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

fun printActivity(lastActivity: ActivitySummary) {
  println("https://www.strava.com/activities/" + lastActivity.id)
  println("Distance: ${(lastActivity.distance / 1000.0).format(1)} km")
  println("Duration: ${(lastActivity.elapsed_time / 60.0).format(0)} minutes")
  println("Avg Heartrate: ${lastActivity.average_heartrate}")
  println("Avg Speed: ${lastActivity.average_speed}")
  println("Type: ${lastActivity.type}")
}

suspend fun readLastActivity(): ActivitySummary {
  val activities =
    client.queryList<ActivitySummary>(
      "https://www.strava.com/api/v3/athlete/activities?page=1&per_page=1"
    )

  if (activities.isEmpty()) {
    throw UsageException("No activities found")
  }

  val lastActivity = client.query<ActivitySummary>(
    "https://www.strava.com/api/v3/activities/${activities.first().id}"
  )
  return lastActivity
}

runScript {
  val lastActivity = readLastActivity()

  show(staticMap {
    style = StaticMapBuilder.Satellite
    route(lastActivity.map?.polyline)
  })

  printActivity(lastActivity)
}
