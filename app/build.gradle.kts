import java.security.KeyStore
import java.security.MessageDigest

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.carenest.provider"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    defaultConfig {
        applicationId = "com.carenest.provider"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

tasks.register("printSha1") {
    group = "help"
    description = "Prints the SHA-1 and SHA-256 fingerprints of the project's signing certificate for Google Cloud Console."
    val keystoreFile = layout.projectDirectory.file("debug.keystore").asFile
    doLast {
        if (keystoreFile.exists()) {
            val keystore = KeyStore.getInstance(KeyStore.getDefaultType())
            keystoreFile.inputStream().use { stream -> keystore.load(stream, "android".toCharArray()) }
            val cert = keystore.getCertificate("androiddebugkey")
            val sha1Bytes = MessageDigest.getInstance("SHA-1").digest(cert.encoded)
            val sha1Hex = sha1Bytes.joinToString(":") { byte -> "%02X".format(byte) }
            val sha256Bytes = MessageDigest.getInstance("SHA-256").digest(cert.encoded)
            val sha256Hex = sha256Bytes.joinToString(":") { byte -> "%02X".format(byte) }

            println("==========================================================")
            println("CARENEST-PROVIDER SIGNING FINGERPRINTS")
            println("Package Name: com.carenest.provider")
            println("Keystore:     ${keystoreFile.absolutePath}")
            println("SHA-1:       $sha1Hex")
            println("SHA-256:     $sha256Hex")
            println("==========================================================")
        } else {
            println("Keystore file not found at ${keystoreFile.absolutePath}")
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":designsystem"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:home"))
    implementation(project(":feature:request"))
    implementation(project(":feature:account"))
    implementation(project(":feature:earnings"))
    implementation(project(":feature:payouts"))
    implementation(project(":feature:chat"))

    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.navigation3)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.bundles.compose)
    implementation(libs.androidx.appcompat)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
