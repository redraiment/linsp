plugins {
    alias(zzp.plugins.kotlin.multiplatform)
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
                entryPoint = "me.zzp.main"
            }
        }
    }

    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation(zzp.utils.clikt)
            }
        }
        val nativeTest by getting
    }
}
