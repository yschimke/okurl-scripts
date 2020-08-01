
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version Versions.kotlin
  `maven-publish`
  distribution
  id("com.github.ben-manes.versions") version "0.28.0"
  id("net.nemerosa.versioning") version "2.12.1"
  id("com.diffplug.gradle.spotless") version "3.28.1"
}

repositories {
  jcenter()
  mavenCentral()
//  maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
  maven(url = "https://jitpack.io")
//  maven(url = "https://repo.maven.apache.org/maven2")
//  maven(url = "https://dl.bintray.com/kotlin/kotlin-eap/")
//  maven(url = "https://dl.bintray.com/kotlin/kotlin-dev/")
//  maven(url = "https://repo.spring.io/milestone/")
//  maven(url = "https://dl.bintray.com/reactivesocket/RSocket/")
//  maven(url = "https://oss.sonatype.org/content/repositories/releases/")
//  maven(url = "https://dl.bintray.com/yschimke/baulsupp.com/")
//  maven(url = "https://packages.atlassian.com/maven-public")
}

group = "com.github.yschimke"
description = "OkHttp Kotlin CLI"
version = versioning.info.display

base {
  archivesBaseName = "okscript"
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
  withType(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.apiVersion = "1.4"
    kotlinOptions.languageVersion = "1.4"

    kotlinOptions.allWarningsAsErrors = false
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable")
  }
}

tasks {
  withType(Tar::class) {
    compression = Compression.NONE
  }
}

dependencies {
  implementation("com.github.yschimke:oksocial-output:5.1")
  implementation("com.github.yschimke:okurl:2.12")
  implementation("com.squareup.okhttp3:logging-interceptor:4.8.0")
  implementation("com.squareup.okhttp3:okhttp:4.8.0")
  implementation("com.squareup.okhttp3:okhttp-brotli:4.8.0")
  implementation("com.squareup.okhttp3:okhttp-dnsoverhttps:4.8.0")
  implementation("com.squareup.okhttp3:okhttp-sse:4.8.0")
  implementation("com.squareup.okhttp3:okhttp-tls:4.8.0")
  implementation("com.squareup.moshi:moshi:1.9.3")
  implementation("com.squareup.moshi:moshi-adapters:1.9.3")
  implementation("com.squareup.moshi:moshi-kotlin:1.9.3")
  implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies:1.4.0-rc")
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.0-rc")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.0-rc")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8-1.4.0-rc")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.3.8-1.4.0-rc")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.8-1.4.0-rc")
  implementation("org.slf4j:slf4j-api:2.0.0-alpha0")
  implementation("org.slf4j:slf4j-jdk14:2.0.0-alpha0")
  implementation("org.zeroturnaround:zt-exec:1.11")
  implementation("com.formdev:svgSalamander:1.1.2.1")
  implementation("org.jfree:jfreesvg:3.4")
  implementation("info.picocli:picocli:4.4.0")

  implementation("org.jetbrains.kotlin:kotlin-script-util:1.4.0-rc") {
    exclude(module = "kotlin-compiler")
  }

  testImplementation("org.jetbrains.kotlin:kotlin-test:1.4.0-rc")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.4.0-rc")
  testImplementation("com.squareup.okhttp3:mockwebserver:4.7.0")

  testRuntime("org.slf4j:slf4j-jdk14:2.0.0-alpha0")
}

val sourcesJar by tasks.creating(Jar::class) {
  classifier = "sources"
  from(kotlin.sourceSets["main"].kotlin)
}

val javadocJar by tasks.creating(Jar::class) {
  classifier = "javadoc"
  from("$buildDir/javadoc")
}

val jar = tasks["jar"] as org.gradle.jvm.tasks.Jar

spotless {
  kotlinGradle {
    ktlint("0.31.0").userData(mutableMapOf("indent_size" to "2", "continuation_indent_size" to "2"))
    trimTrailingWhitespace()
    endWithNewline()
  }
}

distributions {
  getByName("main") {
    contents {
      from("${rootProject.projectDir}") {
        include("README.md", "LICENSE")
      }
      from("${rootProject.projectDir}/src/main/scripts") {
        fileMode = Integer.parseUnsignedInt("755", 8)
        into("bin")
      }
      from("${rootProject.projectDir}/src/test/kotlin/commands") {
        fileMode = Integer.parseUnsignedInt("755", 8)
        exclude("local")
        into("bin")
      }
//      from("${rootProject.projectDir}/src/main/resources") {
//        into("scripts")
//      }
//      into("lib") {
//        from(jar)
//      }
//      into("lib") {
//        from(configurations.runtimeClasspath)
//      }
    }
  }
}
