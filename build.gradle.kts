import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.6.10"
}

repositories {
  jcenter()
  mavenCentral()
  maven(url = "https://jitpack.io")
}

group = "com.github.yschimke"
description = "OkHttp Kotlin Scripts"

base {
  archivesBaseName = "okscripts"
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

tasks {
  withType(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.apiVersion = "1.6"
    kotlinOptions.languageVersion = "1.6"

    kotlinOptions.allWarningsAsErrors = false
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable", "-jvm-target=17")
  }
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-main-kts:1.6.10")
  implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.6.10")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
}
