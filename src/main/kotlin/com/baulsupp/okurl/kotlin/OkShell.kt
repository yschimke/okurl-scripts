package com.baulsupp.okurl.kotlin

import com.baulsupp.oksocial.output.ConsoleHandler
import com.baulsupp.oksocial.output.OutputHandler
import com.baulsupp.oksocial.output.SimpleResponseExtractor
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.credentials.Token
import com.baulsupp.okurl.location.BestLocation
import com.baulsupp.okurl.location.Location
import com.baulsupp.okurl.location.LocationSource
import com.baulsupp.okurl.moshi.Rfc3339InstantJsonAdapter
import com.baulsupp.okurl.okhttp.OkHttpResponseExtractor
import com.baulsupp.okurl.services.mapbox.model.MapboxLatLongAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

val client: OkHttpClient by lazy { OkHttpClient() }
val outputHandler: OutputHandler<Response> by lazy { ConsoleHandler<Response>(OkHttpResponseExtractor()) }
val locationSource: LocationSource by lazy { BestLocation(outputHandler) }

inline fun <reified T> query(
  url: String,
  tokenSet: Token = DefaultToken,
  noinline init: Request.Builder.() -> Unit = {}
): T {
  return query(request(url, tokenSet, init))
}

inline fun <reified T> query(request: Request): T {
  val stringResult = runBlocking { client.queryForString(request) }

  return moshi.adapter(T::class.java).fromJson(stringResult)!!
}

val moshi = Moshi.Builder()
  .add(Location::class.java, MapboxLatLongAdapter().nullSafe())
  .add(KotlinJsonAdapterFactory())
  .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
  .add(Instant::class.java, Rfc3339InstantJsonAdapter.nullSafe())
  .build()!!

fun warmup(vararg urls: String) {
  client.warmup(*urls)
}

fun location(): Location? = runBlocking { locationSource.read() }

fun show(url: String) {
  runBlocking {
    val response = client.execute(request(url))

    outputHandler.showOutput(response)
  }
}

suspend fun showOutput(response: Response) {
  outputHandler.showOutput(response)
}

fun newWebSocket(url: String, listener: WebSocketListener): WebSocket = client.newWebSocket(
  Request.Builder().url(url).build(), listener
)

var dateOnlyformat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

fun epochSecondsToDate(seconds: Long) = dateOnlyformat.format(Date(seconds * 1000))!!

val terminalWidth: Int? by lazy { runBlocking { (outputHandler as? ConsoleHandler<Response>)?.terminalWidth() } }

fun jsonPostRequest(url: String, body: String): Request =
  requestBuilder(url, DefaultToken).post(
    body.toRequestBody(JSON)
  ).build()

var args: List<String> = listOf()

val simpleOutput = ConsoleHandler(SimpleResponseExtractor)
