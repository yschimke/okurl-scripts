#!/usr/bin/env -S kotlinc-jvm -nowarn -script

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:1.0.0")
@file:CompilerOptions("-jvm-target", "1.8")

import com.baulsupp.okscript.client
import com.baulsupp.okscript.execute
import com.baulsupp.okscript.outputHandler
import com.baulsupp.okscript.query
import com.baulsupp.okscript.request
import com.baulsupp.okscript.runScript
import com.baulsupp.okscript.showOutput
import kotlinx.coroutines.async

val size = "preview_gif"

data class Image(val url: String?)
data class ImageResult(
  val type: String,
  val id: String,
  val url: String,
  val images: Map<String, Image>
)
data class SearchResults(val data: List<ImageResult>)

runScript {
  val urls = client.query<SearchResults>(
    "https://api.giphy.com/v1/gifs/search?q=" + args.joinToString(
      "+"
    )
  ).data.mapNotNull { it.images[size]?.url }

  val fetches =
    urls.map {
      async {
        Pair(it, client.execute(request(it)))
      }
    }

  fetches.forEach {
    val (url, image) = it.await()
    outputHandler.info(url)
    showOutput(image)
  }
}
