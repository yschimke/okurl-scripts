import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.72"
}

repositories {
  jcenter()
  mavenCentral()
  maven(url = "https://jitpack.io")
//  maven(url = "https://dl.bintray.com/kotlin/kotlin-eap/")
//  maven(url = "https://dl.bintray.com/kotlin/kotlin-dev/")
}

group = "com.github.yschimke"
description = "OkHttp Kotlin Scripts"

base {
  archivesBaseName = "okscripts"
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
  withType(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.apiVersion = "1.3"
    kotlinOptions.languageVersion = "1.3"

    kotlinOptions.allWarningsAsErrors = false
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable -jvm-target=1.8")
  }
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-main-kts:1.3.72")
  implementation("org.jetbrains.kotlin:kotlin-main-kts:1.3.72")
  implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.3.72")
//  implementation("org.jetbrains.kotlin:kotlin-scripting-common:1.3.72")
//  implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:1.3.72")
//  implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:1.3.72")
//  implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies:1.3.72")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8")
}
