#!/usr/bin/env kotlin

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:2.1.0")
@file:CompilerOptions("-jvm-target", "17")

println(Class.forName("com.baulsupp.okscript.WebSocketPrinter"))
println("Hello")
