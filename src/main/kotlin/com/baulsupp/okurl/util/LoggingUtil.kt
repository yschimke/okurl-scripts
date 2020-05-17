package com.baulsupp.okurl.util

import io.netty.util.internal.logging.InternalLoggerFactory
import io.netty.util.internal.logging.JdkLoggerFactory
import okhttp3.internal.http2.Http2
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.LogRecord
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

class LoggingUtil {
  companion object {
    private val activeLoggers = mutableListOf<Logger>()

    fun configureLogging(debug: Boolean, showHttp2Frames: Boolean, sslDebug: Boolean) {
      InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE)

      if (debug || showHttp2Frames || sslDebug) {
        if (sslDebug) {
          System.setProperty("javax.net.debug", "")
        }

        LogManager.getLogManager().reset()
        val handler = object : ConsoleHandler() {
          override fun publish(record: LogRecord) {
            super.publish(record)

            val parameters = record.parameters
            if (sslDebug && record.loggerName == "javax.net.ssl" && parameters != null) {
              System.err.println(parameters[0])
            }
          }
        }

        if (debug) {
          handler.level = Level.ALL
          handler.formatter = OneLineLogFormat()
          val activeLogger = getLogger("")
          activeLogger.addHandler(handler)
          activeLogger.level = Level.ALL

          getLogger("org.zeroturnaround.exec").level = Level.INFO
          getLogger("io.netty").level = Level.INFO
          getLogger("io.netty.resolver.dns").level = Level.FINE
          getLogger("jdk.event.security").level = Level.INFO
          getLogger("org.conscrypt").level = Level.INFO
        } else {
          if (showHttp2Frames) {
            val activeLogger = getLogger(Http2::class.java.name)
            activeLogger.level = Level.FINE
            handler.level = Level.FINE
            handler.formatter = MessageFormatter
            activeLogger.addHandler(handler)
          }

          if (sslDebug) {
            val activeLogger = getLogger("javax.net.ssl")

            activeLogger.level = Level.FINEST
            handler.level = Level.FINEST
            handler.formatter = MessageFormatter
            activeLogger.addHandler(handler)
          }
        }
      }

      getLogger("io.netty.resolver.dns.DnsServerAddresses").level = Level.SEVERE
      getLogger("com.launchdarkly.eventsource").level = Level.SEVERE
    }

    fun getLogger(name: String): Logger {
      val logger = Logger.getLogger(name)
      activeLoggers.add(logger)
      return logger
    }
  }
}
