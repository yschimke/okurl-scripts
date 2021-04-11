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
@file:DependsOn("com.github.yschimke:okurl:3.2")
@file:DependsOn("com.github.yschimke:okurl-script:2.0.2")
@file:CompilerOptions("-jvm-target", "1.8")

import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CommandStatus
import com.baulsupp.cooee.p.ImageUrl
import com.baulsupp.cooee.sdk.SimpleServer
import com.baulsupp.okscript.client
import com.baulsupp.okurl.kotlin.execute
import com.baulsupp.okurl.kotlin.request
import com.baulsupp.okurl.moshi.Rfc3339InstantJsonAdapter
import com.baulsupp.okurl.services.mapbox.model.MapboxLatLongAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.*
import java.nio.charset.StandardCharsets
import java.util.*

data class Symbol(
  val indices: List<Int>? = null,
  val text: String? = null
)

data class UserMention(
  val id: Int? = null,
  val id_str: String? = null,
  val indices: List<Int?>? = null,
  val name: String? = null,
  val screen_name: String? = null
)

data class VideoInfo(
  val aspect_ratio: List<Int>? = null,
  val duration_millis: Int? = null,
  val variants: List<Variant>? = null
) {
  data class Variant(
    val bitrate: Int? = null,
    val content_type: String? = null,
    val url: String? = null
  )
}

data class Url(
  val display_url: String? = null,
  val expanded_url: String? = null,
  val indices: List<Int?>? = null,
  val unwound: Unwound? = null,
  val url: String? = null
) {
  data class Unwound(
    val description: String? = null,
    val status: Int? = null,
    val title: String? = null,
    val url: String? = null
  )
}

data class HashTag(
  val indices: List<Int>? = null,
  val text: String? = null
)

data class Media(
  val display_url: String? = null,
  val expanded_url: String? = null,
  val id: Long? = null,
  val id_str: String? = null,
  val indices: List<Int?>? = null,
  val media_url: String? = null,
  val media_url_https: String? = null,
  val sizes: Sizes? = null,
  val type: String? = null,
  val url: String? = null,
  val video_info: VideoInfo? = null
) {
  data class Sizes(
    val large: Size? = null,
    val medium: Size? = null,
    val small: Size? = null,
    val thumb: Size? = null
  ) {
    data class Size(
      val h: Int? = null,
      val resize: String? = null,
      val w: Int? = null
    )
  }
}

data class User(
  val description: String? = null,
  val followers_count: Int? = null,
  val id_str: String? = null,
  val name: String? = null,
  val profile_background_image_url_https: String? = null,
  val profile_image_url_https: String? = null,
  val `protected`: Boolean? = null,
  val screen_name: String? = null,
  val url: String? = null,
  val verified: Boolean? = null
)

data class Place(
  val bounding_box: BoundingBox? = null,
  val country: String? = null,
  val country_code: String? = null,
  val full_name: String? = null,
  val id: String? = null,
  val name: String? = null,
  val place_type: String? = null,
  val url: String? = null
) {
  data class BoundingBox(
    val coordinates: List<List<List<Double>>>,
    val type: String
  )
}

data class Entities(
  val media: List<Media>? = null,
  val hashtags: List<HashTag>? = null,
  val urls: List<Url>? = null,
  val userMention: List<UserMention>? = null,
  val symbols: List<Symbol>? = null
)

data class Tweet(
  val created_at: String? = null,
  val favorite_count: Int? = null,
  val id_str: String? = null,
  val in_reply_to_screen_name: String? = null,
  val in_reply_to_status_id_str: String? = null,
  val in_reply_to_user_id_str: String? = null,
  val is_quote_status: Boolean? = null,
  val lang: String? = null,
  val quote_count: Int? = null,
  val reply_count: Int? = null,
  val retweet_count: Int? = null,
  val retweeted: Boolean? = null,
  val source: String? = null,
  val text: String? = null,
  val timestamp_ms: String? = null,
  val truncated: Boolean? = null,
  val user: User? = null,
  val place: Place? = null,
  val entities: Entities? = null,
  val extended_entities: Entities? = null
)

val moshi = Moshi.Builder()
  .add(MapboxLatLongAdapter())
  .add(KotlinJsonAdapterFactory())
  .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
  .add(Rfc3339InstantJsonAdapter())
  .build()!!

val tweetAdapter = moshi.adapter(Tweet::class.java)!!

fun parseTweet(it: String): Tweet = tweetAdapter.fromJson(it)!!

suspend fun search(query: String?): Flow<Tweet> {
  val s = client.execute(request(url = "https://stream.twitter.com/1.1/statuses/filter.json?track=$query"))

  val r = s.body!!.source()
    .inputStream()
    .bufferedReader(StandardCharsets.UTF_8)

  return r.lineSequence()
    .asFlow()
    .onCompletion {
      s.close()
    }
    .mapNotNull {
      @Suppress("BlockingMethodInNonBlockingContext")
      parseTweet(it)
    }
}

val server = SimpleServer(debug = false, local = false)

server.exportSingleCommand("tweetSearch") { request ->
  search(request.parsed_command.drop(1).joinToString("+")).map { tweet ->
    val imageUrl = null
    // TODO sort out client auth with cooee command
//    tweet.extended_entities?.media?.firstOrNull()?.media_url_https?.let {
//      ImageUrl(url = it)
//    }
    CommandResponse(message = tweet.text, url = "https://twitter.com/${tweet.user?.screen_name}/status/${tweet.id_str}", status = CommandStatus.DONE, image_url = imageUrl)
  }
}
