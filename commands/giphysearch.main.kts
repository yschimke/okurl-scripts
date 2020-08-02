#!/usr/bin/env kotlin

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:1.0.0")

import com.baulsupp.okscript.client
import com.baulsupp.okscript.execute
import com.baulsupp.okscript.query
import com.baulsupp.okscript.request
import com.baulsupp.okscript.runScript
import com.baulsupp.okscript.showOutput
import com.baulsupp.okurl.services.giphy.model.SearchResults
import kotlinx.coroutines.async

val size = "preview_gif"

runScript {
  val urls = client.query<SearchResults>(
    "https://api.giphy.com/v1/gifs/search?q=" + args.joinToString(
      "+"
    )
  ).data.mapNotNull { it.images[size]?.url }

  val fetches =
    urls.map {
      async {
        client.execute(request(it))
      }
    }

  fetches.forEach {
    showOutput(it.await())
  }
}
