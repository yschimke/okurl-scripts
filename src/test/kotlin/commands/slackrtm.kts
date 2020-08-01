#!/usr/bin/env kotlin

import com.baulsupp.okurl.kotlin.*
import com.baulsupp.okurl.services.slack.model.RtmConnect
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

val wsClient = client.newBuilder().readTimeout(0, TimeUnit.MINUTES).build()

val start = runBlocking { wsClient.query<RtmConnect>("https://slack.com/api/rtm.connect") }

val printer = WebSocketPrinter(outputHandler)
val ws = newWebSocket(start.url, printer)

printer.waitForExit()
