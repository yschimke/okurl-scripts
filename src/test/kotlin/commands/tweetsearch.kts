#!/usr/bin/env kotlin

import com.baulsupp.okurl.kotlin.client
import com.baulsupp.okurl.kotlin.execute
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.request
import com.baulsupp.okurl.kotlin.showOutput
import com.baulsupp.okurl.services.twitter.model.SearchResults
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.net.URLEncoder

var argumentString = args.joinToString("+") { URLEncoder.encode(it, "UTF-8") }

suspend fun run() {
  val results = client.query<SearchResults>(
    "https://api.twitter.com/1.1/search/tweets.json?tweet_mode=extended&q=${argumentString}"
  )

  val images = coroutineScope {
    results.statuses.map {
      it.id_str to it.entities?.media?.map {
        async {
          client.execute(request("${it.media_url_https}:thumb"))
        }
      }
    }
      .toMap()
  }

  results.statuses.forEach { tweet ->
    println("%-20s: %s".format(tweet.user.screen_name, tweet.full_text))

    images[tweet.id_str]?.forEach {
      showOutput(it.await())
      println()
    }
  }
}

runBlocking {
  run()
}
