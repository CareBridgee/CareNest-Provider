import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.carenest.provider.core"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    val properties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { properties.load(it) }
    }
    val baseUrl = properties.getProperty("BASE_URL") ?: "https://api.carenest.com/"

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    //navigation
    implementation(libs.bundles.navigation3)

    //di
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.bundles.ktor)
    implementation(libs.kotlinx.serialization)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    testImplementation(libs.ktor.client.mock)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
