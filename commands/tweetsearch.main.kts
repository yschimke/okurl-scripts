#!/usr/bin/env okscript

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:1.0.0")

import com.baulsupp.okscript.client
import com.baulsupp.okscript.execute
import com.baulsupp.okscript.query
import com.baulsupp.okscript.request
import com.baulsupp.okscript.runScript
import com.baulsupp.okscript.showOutput
import com.baulsupp.okurl.services.twitter.model.SearchResults
import kotlinx.coroutines.async
import java.net.URLEncoder

var argumentString = args.joinToString("+") { URLEncoder.encode(it, "UTF-8") }

runScript {
  val results = client.query<SearchResults>(
    "https://api.twitter.com/1.1/search/tweets.json?tweet_mode=extended&q=${argumentString}"
  )

  val images = results.statuses.map {
      it.id_str to it.entities?.media?.map {
        async {
          client.execute(request("${it.media_url_https}:medium"))
        }
      }
    }
      .toMap()

  results.statuses.forEach { tweet ->
    println("%-20s: %s".format(tweet.user.screen_name, tweet.full_text))

    images[tweet.id_str]?.forEach {
      showOutput(it.await())
      println()
    }
  }
}
