#!/usr/bin/env kotlin

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:2.0.2")
@file:CompilerOptions("-jvm-target", "1.8")

import com.baulsupp.okscript.client
import com.baulsupp.okscript.execute
import com.baulsupp.okscript.queryList
import com.baulsupp.okscript.request
import com.baulsupp.okscript.runScript
import okio.buffer
import okio.sink
import java.io.File

data class Show(
  val number: Long,
  val title: String,
  val date: Long,
  val url: String,
  val slug: String,
  val html: String,
  val notesFile: String,
  val displayDate: String,
  val displayNumber: String
) {
  val name: String
    get() = "Syntax $number - ${title.replace("/", "")} - ${displayDate}.mp3"
}

runScript {
  val showList = "https://syntax.fm/api/shows"
  val outputDir = File("tmp").also { it.mkdir() }

  val shows = client.queryList<Show>(showList)

  for (show in shows) {
    download(outputDir, show)
  }
}

suspend fun download(outputDir: File, show: Show) {
  println("Downloading ${show.name} ${show.url}")
  client.execute(request(show.url)).use {
    if (it.code == 200) {
      outputDir.resolve("${show.number}.mp3").sink().buffer().writeAll(it.body!!.source())
      System.err.println("" + show.url + " downloaded: " + it.body?.contentLength())
    } else {
      System.err.println("" + show.url + " error: " + it.code)
    }
  }
}
