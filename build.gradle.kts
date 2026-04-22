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

    // 4. GUI: ScalaFX
    implementation("org.scalafx:scalafx_3:20.0.0-R31")

    // 5. Testing: ScalaTest
    testImplementation("org.scalatest:scalatest_3:3.2.17")
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

tasks {
    val runEventLoop by registering(JavaExec::class) {
        group = "application"
        mainClass.set("pcd.ass02.fsstat.eventLoop.eventLoopFSStat")
        classpath = sourceSets["main"].runtimeClasspath
    }
    /*val runReactive by registering(JavaExec::class) {
        group = "application"
        mainClass.set("pcd.ass02.fsstat.reactive.Main")
        classpath = sourceSets["main"].runtimeClasspath
    }

    val runVThreads by registering(JavaExec::class) {
        group = "application"
        mainClass.set("pcd.ass02.fsstat.vthreads.Main")
        classpath = sourceSets["main"].runtimeClasspath
        jvmArgs("-Djdk.tracePinnedThreads=full")
    }*/
}