import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.carenest.request"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            type = "String",
            name = "MAPBOX_ACCESS_TOKEN",
            value = "\"${gradleLocalProperties(rootDir, providers).getProperty("MAPBOX_ACCESS_TOKEN")}\"",
        )
        buildConfigField(
            type = "String",
            name = "LOCATION_IQ_TOKEN",
            value = "\"${gradleLocalProperties(rootDir, providers).getProperty("location_iq_token", "")}\"",
        )
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

}

dependencies {
    implementation(project(":core"))
    implementation(project(":designsystem"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.navigation3.runtime)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Lifecycle and ViewModel
    implementation(libs.bundles.lifecycle)

    // Image loading
    implementation(libs.bundles.coil)

    // Camera and Barcode Scanning
    implementation(libs.bundles.camera)
    implementation(libs.barcode.scanning)

    // Networking
    implementation(libs.bundles.ktor)
    implementation(libs.kotlinx.serialization)

    // Mapbox
    implementation(libs.bundles.mapbox)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
