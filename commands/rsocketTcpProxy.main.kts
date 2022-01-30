#!/usr/bin/env kotlin

@file:Repository("https://jcenter.bintray.com")
@file:DependsOn("io.rsocket.kotlin:rsocket-transport-ktor-client-jvm:0.12.0")
@file:DependsOn("io.ktor:ktor-client-cio-jvm:1.4.3")
@file:CompilerOptions("-jvm-target", "17")

import io.ktor.client.HttpClient
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.rsocket.kotlin.core.RSocketServer
import io.rsocket.kotlin.core.RSocketConnector
import io.rsocket.kotlin.logging.LoggingLevel
import io.rsocket.kotlin.logging.PrintLogger
import io.rsocket.kotlin.transport.ktor.serverTransport
import io.rsocket.kotlin.transport.ktor.client.rSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import io.ktor.client.engine.cio.CIO as ClientCIO
import io.ktor.client.features.websocket.WebSockets as ClientWebSockets
import io.rsocket.kotlin.transport.ktor.client.RSocketSupport as ClientRSocketSupport

suspend fun runProxy() {
  ActorSelectorManager(Dispatchers.IO).use { selector ->
    val transport = aSocket(selector).tcp().serverTransport(port = 9000)

    RSocketServer().bind(transport) {
      val url = config.setupPayload.data.readText()
      val httpClient = HttpClient(ClientCIO) {
        install(ClientWebSockets)
        install(ClientRSocketSupport) {
          connector = RSocketConnector {
            loggerFactory = PrintLogger.withLevel(LoggingLevel.DEBUG)
            connectionConfig {
              payloadMimeType = config.payloadMimeType
            }
          }
        }
      }
      println("route: $url")
      runBlocking { httpClient.rSocket(url) }
    }.join()
  }
}

runBlocking {
  println("running at tcp://localhost:9000")
  runProxy()
}
