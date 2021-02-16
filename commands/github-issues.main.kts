#!/usr/bin/env kotlin

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:1.0.0")
@file:CompilerOptions("-jvm-target", "1.8")

import com.baulsupp.okscript.postJsonBody
import com.baulsupp.okscript.queryList
import com.baulsupp.okscript.request
import com.baulsupp.okscript.runScript
import com.baulsupp.okscript.usage
import com.baulsupp.okscript.client

data class User(val login: String)

data class Issue(
  val id: String,
  val html_url: String,
  val title: String,
  val user: User,
  val created_at: String,
  val comments: Int,
)

if (args.size < 2) {
  usage("github-issues.main.kts org repo")
}

val (owner, repo) = args

runScript {
  val results = client.queryList<Issue>("https://api.github.com/repos/$owner/$repo/issues")

  for (issue in results) {
    val created = issue.created_at.substring(0, 10)
    println("${issue.id}\t${issue.user.login}\t$created\t${issue.comments}")
    println("\t${issue.title}")
    println("\t${issue.html_url}")
    println()
  }
}
