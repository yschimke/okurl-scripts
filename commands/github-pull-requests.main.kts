#!/usr/bin/env -S kotlinc-jvm -nowarn -script

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:1.0.0")
@file:CompilerOptions("-jvm-target", "1.8")

import com.baulsupp.okscript.postJsonBody
import com.baulsupp.okscript.query
import com.baulsupp.okscript.request
import com.baulsupp.okscript.runScript
import com.baulsupp.okscript.usage

data class Commit(val oid: String)
data class CommitNode(val commit: Commit)
data class Commits(val nodes: List<CommitNode>)
data class PullRequest(
  val author: Author,
  val title: String,
  val permalink: String,
  val updatedAt: String,
  val commits: Commits
) {
  val commit = commits.nodes.last().commit
}

data class PullRequests(val nodes: List<PullRequest>)
data class Author(
  val name: String?,
  val login: String
)

data class Repository(val pullRequests: PullRequests)
data class Data(val repository: Repository)
data class PullRequestResults(val data: Data) {
  val pullRequests = this.data.repository.pullRequests.nodes
}

data class Query(val query: String)

if (args.size < 2) {
  usage("github-pull-requests.kts org repo")
}

val (owner, repo) = args

val query = """
query {
  repository(name: "$repo", owner: "$owner") {
    pullRequests(first: 10, states:OPEN, orderBy:{field: UPDATED_AT, direction:DESC}) {
      nodes {
        author {
          login
          ... on User {
            name
          }
        }
        title
        permalink
        updatedAt
        commits(last:1) {
          nodes {
            commit {
              oid
            }
          }
        }
      }
    }
  }
}
"""

runScript {
  val results = query<PullRequestResults>(request {
    url("https://api.github.com/graphql")
    header("Accept", "application/vnd.github.antiope-preview")
    postJsonBody(Query(query))
  })

  results.pullRequests.forEach {
    println("Title: ${it.title}")
    println("Author: ${it.author.login}")
    println("Commit: ${it.commit.oid}")
    println()
  }
}
