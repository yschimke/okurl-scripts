#!/usr/bin/env kotlin

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:2.0.2")
@file:CompilerOptions("-jvm-target", "1.8")

println(Class.forName("com.baulsupp.okscript.WebSocketPrinter"))
println("Hello")
