#!/usr/bin/env okscript

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:1.0.0")
@file:CompilerOptions("-jvm-target", "1.8")

import com.baulsupp.okscript.WebSocketPrinter
import com.baulsupp.okscript.client
import com.baulsupp.okscript.newWebSocket
import com.baulsupp.okscript.outputHandler
import com.baulsupp.okscript.query
import com.baulsupp.okurl.services.slack.model.RtmConnect
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit.MINUTES

runBlocking {
  subscribe()
}

suspend fun subscribe() {
  val wsClient = client.newBuilder()
    .readTimeout(0, MINUTES)
    .build()

  val start = wsClient.query<RtmConnect>("https://slack.com/api/rtm.connect")

  val printer = WebSocketPrinter(outputHandler)
  val ws = newWebSocket(start.url, printer)

  printer.waitForExit()
}
