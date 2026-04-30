plugins {
    scala
    java
}

group = "pcd.ass02"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // 1. Scala 3 Standard Library
    implementation("org.scala-lang:scala3-library_3:3.3.1")

    // 2. Event-Loop: Vert.x
    implementation("io.vertx:vertx-core:4.5.4")

    // 3. Reactive Programming: RxJava 3
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<ScalaCompile> {
    scalaCompileOptions.additionalParameters.add("-deprecation")
    scalaCompileOptions.additionalParameters.add("-feature")
    scalaCompileOptions.additionalParameters.add("-new-syntax") // Opzionale: per forzare la nuova sintassi
}
