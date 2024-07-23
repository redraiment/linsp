plugins {
    kotlin("multiplatform") version "2.0.0"
}

group = "me.zzp"
version = "1.0.0-SNAPSHOT"
description = "Lisp in Lisp"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "me.zzp.linsp.main"
            }
        }
    }

    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.5.1")
                implementation("com.github.ajalt.clikt:clikt:4.4.0")
            }
        }
        val nativeTest by getting
    }
}
