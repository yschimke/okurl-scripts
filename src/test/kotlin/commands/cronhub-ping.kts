#!/usr/bin/env kotlin

import com.baulsupp.okurl.kotlin.usage
import com.baulsupp.okurl.services.cronhub.ping
import kotlinx.coroutines.runBlocking

if (args.isEmpty()) usage("must supply uuid")

suspend fun run() = ping(args[0])

runBlocking { run() }
