#!/usr/bin/env okscript

import com.baulsupp.oksocial.kotlin.arguments
import com.baulsupp.oksocial.kotlin.client
import com.baulsupp.oksocial.kotlin.execute
import com.baulsupp.oksocial.kotlin.query
import com.baulsupp.oksocial.kotlin.request
import com.baulsupp.oksocial.kotlin.showOutput
import com.baulsupp.oksocial.services.giphy.model.SearchResults
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking

val size = "preview_gif"

runBlocking {
  val urls = client.query<SearchResults>(
    "https://api.giphy.com/v1/gifs/search?q=" + arguments.joinToString(
      "+")).data.mapNotNull { it.images[size]?.url }

  val fetches = urls.map {
    async {
      client.execute(request(it))
    }
  }

  fetches.forEach {
    showOutput(it.await())
  }
}
