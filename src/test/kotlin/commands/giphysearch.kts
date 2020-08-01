#!/usr/bin/env kotlin

import com.baulsupp.okurl.kotlin.*
import com.baulsupp.okurl.services.giphy.model.SearchResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient

val size = "preview_gif"

suspend fun run() {
  val urls = client.query<SearchResults>(
    "https://api.giphy.com/v1/gifs/search?q=" + args.joinToString(
      "+"
    )
  ).data.mapNotNull { it.images[size]?.url }

  val fetches = coroutineScope {
    urls.map {
      async {
        client.execute(request(it))
      }
    }
  }

  fetches.forEach {
    showOutput(it.await())
  }
}

runBlocking {
  run()
}
