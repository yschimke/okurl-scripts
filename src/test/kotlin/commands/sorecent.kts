#!/usr/bin/env kotlin

import com.baulsupp.okurl.kotlin.*
import com.baulsupp.okurl.services.stackexchange.model.Questions
import kotlinx.coroutines.runBlocking
import kotlin.math.max

var questions = runBlocking {
  client.query<Questions>("https://api.stackexchange.com/2.2/questions/unanswered/my-tags?order=desc&sort=creation&site=stackoverflow")
}

val titleWidth = max(50, (terminalWidth ?: 0) - 80)
for (q in questions.items) {
  val time = epochSecondsToDate(q.creation_date)
  val url = q.link.replace("(.*)/.*".toRegex(), "$1")
  println("%$titleWidth.${titleWidth}s %-18.18s %4.4s %-10s %s".format(q.title, q.tags.joinToString(), q.answer_count, time, url))
}
