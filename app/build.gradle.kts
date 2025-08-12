import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

android {
    namespace = "com.niclauscott.jetdrive"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.niclauscott.jetdrive"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"${localProperties["BASE_URL"]}\"")
        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${localProperties["GOOGLE_CLIENT_ID"]}\"")
        manifestPlaceholders["google_client_id"] = localProperties["GOOGLE_CLIENT_ID"] ?: ""
    }
    signingConfigs {
        create("release") {
            storeFile = file("/Users/mac/my-release-key.jks")
            storePassword = project.property("KEYSTORE_PASSWORD") as String
            keyAlias = project.property("KEY_ALIAS") as String
            keyPassword = project.property("KEY_PASSWORD") as String
        }

        getByName("debug") {
            storeFile = file("/Users/mac/my-release-key.jks")
            storePassword = project.property("KEYSTORE_PASSWORD") as String
            keyAlias = project.property("KEY_ALIAS") as String
            keyPassword = project.property("KEY_PASSWORD") as String

        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.koin.startUp)
    implementation(libs.koin.compose)
    implementation(libs.koin.workmanager)
    implementation(libs.koin.navGraph)

    implementation(libs.coil.compose)
    implementation(libs.coil)

    implementation(libs.compose.viemodel)
    implementation(libs.kotlinx.serialization)

    implementation(libs.ktor.core)
    implementation(libs.ktor.cio)
    implementation(libs.ktor.android)
    implementation(libs.ktor.auth)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.content.negotiation)
    implementation(libs.ktor.serialization.json)

    implementation(libs.material3.adaptive)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.androidx.work.runtime.ktx)

    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)

    implementation(libs.navigation.compose)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.material.icon.extended)

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.nav3.runtime)
    implementation(libs.nav3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.nav3)

    implementation(libs.google.play.android.gms)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.play.service.auth)
    implementation(libs.google.android.identity)

    implementation(libs.reorderable)

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.media3.ui.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}