#!/usr/bin/env okscript

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:1.0.0")
@file:CompilerOptions("-jvm-target", "1.8")

println(Class.forName("com.baulsupp.okscript.WebSocketPrinter"))
println("Hello")
