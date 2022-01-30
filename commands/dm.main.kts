#!/usr/bin/env kotlin

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:2.1.0")
@file:CompilerOptions("-jvm-target", "17")

import com.baulsupp.okscript.client
import com.baulsupp.okscript.execute
import com.baulsupp.okscript.postJsonBody
import com.baulsupp.okscript.query
import com.baulsupp.okscript.request
import com.baulsupp.okscript.runScript

var target = args[0]
var message = args.drop(1)

data class Friend(val id_str: String, val screen_name: String, val name: String)

data class Target(val recipient_id: String)
data class MessageData(val text: String)
data class MessageCreate(val target: Target, val message_data: MessageData)
data class DmEvent(val type: String = "message_create", val message_create: MessageCreate)
data class DmRequest(val event: DmEvent) {
  companion object {
      fun simple(target: String, message: String) = DmRequest(DmEvent(message_create = MessageCreate(target = Target(target), message_data = MessageData(text=message))))
  }
}

runScript {
  val friend = client.query<Friend>(
    "https://api.twitter.com/1.1/users/show.json?screen_name=$target"
  )

  client.execute(request("https://api.twitter.com/1.1/direct_messages/events/new.json") {
    postJsonBody(DmRequest.simple(friend.id_str, message.joinToString(" ")))
  })
}
