package com.baulsupp.okurl.kotlin

import com.baulsupp.oksocial.output.UsageException

fun usage(msg: String): Nothing = throw UsageException(msg)

inline fun <T, R> Iterable<T>.flatMapMeToo(function: (T) -> Iterable<R>): List<R> {
  return this.map { function(it) }.flatten()
}
