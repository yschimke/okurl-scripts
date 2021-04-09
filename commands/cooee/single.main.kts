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
@file:CompilerOptions("-jvm-target", "1.8")

import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.sdk.SimpleServer
import kotlinx.coroutines.flow.flowOf

val server = SimpleServer(debug = false, local = false)

server.exportSingleCommand("welcome") {
  flowOf(CommandResponse(message = "Welcome"))
}
