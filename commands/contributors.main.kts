#!/usr/bin/env kotlin

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okscript:0.12")

import com.baulsupp.okscript.client
import com.baulsupp.okscript.queryList
import com.baulsupp.okscript.runScript

data class Contributor(val login: String, val contributions: Int, val avatar_url: String, val url: String)

val repo = args.getOrElse(0) { "square/okhttp" }

suspend fun queryContributors() =
  client.queryList<Contributor>("https://api.github.com/repos/$repo/contributors")

runScript {
  queryContributors().forEach {
    println("${it.login}: ${it.contributions}")
  }
}
