#!/usr/bin/env okscript

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:1.0.0")

import com.baulsupp.okscript.jsonPostRequest
import com.baulsupp.okscript.query
import com.baulsupp.okscript.runScript
import com.baulsupp.okurl.services.dropbox.model.DropboxFileList

runScript {
  val path = args.firstOrNull() ?: ""
  val files = query<DropboxFileList>(
    jsonPostRequest("https://api.dropboxapi.com/2/files/list_folder", "{\"path\": \"$path\"}")
  )

  for (file in files.entries) {
    println("%-25s %-10d".format(file.name, file.size))
  }
}
