import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    // alias(libs.plugins.kotlin.android) // No longer required with built-in Kotlin in AGP 9.0
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.play.publisher)
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

android {
    namespace = "com.germanverbmaster.android"
    compileSdk = 37
    ndkVersion = "29.0.14206865"

    defaultConfig {
        applicationId = "com.germanverbmaster.android"
        minSdk = 26
        targetSdk = 37
        versionCode = 29
        versionName = "0.2.08"

        buildConfigField("String", "SUPABASE_URL",
            "\"${localProps.getProperty("supabase.url", "")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY",
            "\"${localProps.getProperty("supabase.anon.key", "")}\"")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID",
            "\"${localProps.getProperty("google.web.client.id", "YOUR_WEB_CLIENT_ID_HERE")}\"")

        ndk {
            debugSymbolLevel = "FULL"
        }
    }

    signingConfigs {
        create("release") {
            val storeFilePath = localProps.getProperty("signing.storeFile")
            storeFile = if (storeFilePath != null) file(storeFilePath) else file("placeholder.jks")
            storePassword = localProps.getProperty("signing.storePassword")
            keyAlias = localProps.getProperty("signing.keyAlias")
            keyPassword = localProps.getProperty("signing.keyPassword")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    packaging {
        jniLibs {
            // keepDebugSymbols.add("**/libandroidx.graphics.path.so")
            // keepDebugSymbols.add("**/libdatastore_shared_counter.so")
        }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

play {
    serviceAccountCredentials.set(file("play-service-account.json"))
    defaultToAppBundles.set(true)
    track.set("alpha") // "alpha" corresponds to the Closed Testing track
}

room {
    schemaDirectory("$projectDir/schemas")
}

apply(from = "../gradle/tasks/screenshots.gradle.kts")

tasks.withType<Test>().configureEach {
    System.getProperty("roborazzi.test.record")?.let { systemProperty("roborazzi.test.record", it) }
    System.getProperty("roborazzi.test.verify")?.let { systemProperty("roborazzi.test.verify", it) }
}

dependencies {
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.activity.compose)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // Navigation
    implementation(libs.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.workmanager)
    ksp(libs.hilt.workmanager.compiler)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)

    // Ktor
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)

    // Coroutines
    implementation(libs.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // Serialization
    implementation(libs.serialization.json)

    // DataStore
    implementation(libs.datastore.preferences)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.ktx)

    // Charts
    implementation(libs.vico.compose)
    implementation(libs.vico.m3)

    // WorkManager
    implementation(libs.workmanager.ktx)

    // Core
    implementation(libs.core.ktx)

    // Google Login & Credentials
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // ML Kit
    implementation(libs.mlkit.translate)

    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation(libs.roborazzi.compose)

    // Instrumented Testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
}
