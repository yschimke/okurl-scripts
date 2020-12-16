#!/usr/bin/env -S kotlinc-jvm -nowarn -script

@file:Repository("https://jitpack.io")
@file:Repository("https://dl.bintray.com/kotlin/kotlinx")
@file:Repository("https://jcenter.bintray.com")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.4.2-native-mt")
@file:DependsOn("io.rsocket.kotlin:rsocket-core-jvm:0.12.0")
@file:DependsOn("io.rsocket.kotlin:rsocket-transport-ktor-jvm:0.12.0")
@file:DependsOn("io.rsocket.kotlin:rsocket-transport-ktor-client-jvm:0.12.0")
@file:DependsOn("io.ktor:ktor-network-tls:1.4.3")
@file:DependsOn("io.ktor:ktor-client-okhttp:1.4.3")
@file:DependsOn("io.ktor:ktor-client-core-jvm:1.4.3")
@file:DependsOn("io.ktor:ktor-client-cio-jvm:1.4.3")
@file:DependsOn("io.ktor:ktor-server-cio:1.4.3")
@file:CompilerOptions("-jvm-target", "1.8")

import io.ktor.client.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.rsocket.kotlin.core.*
import io.rsocket.kotlin.logging.*
import io.rsocket.kotlin.transport.ktor.*
import io.rsocket.kotlin.transport.ktor.client.*
import kotlinx.coroutines.*
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
