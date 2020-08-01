#!/usr/bin/env kotlin

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okscript:0.12")

import com.baulsupp.okscript.client
import com.baulsupp.okscript.query
import com.baulsupp.okscript.runScript

data class Thread(
  val id: String,
  val snippet: String,
  val historyId: String
)

data class ThreadList(
  val threads: List<Thread>,
  val nextPageToken: String?,
  val resultSizeEstimate: Int
)

val query = args.getOrElse(0) { "label:inbox" }

runScript {
  val threads = client.query<ThreadList>("https://www.googleapis.com/gmail/v1/users/me/threads?q=$query")

  threads.threads.forEach {
    println(it.snippet)
  }
}
