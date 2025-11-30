// build.gradle.kts (:app)

import java.util.Properties
import java.io.FileInputStream

// Cargar propiedades locales
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.ksp)
    jacoco
}

android {
    namespace = "com.miskidev.miskiparqueo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.miskidev.miskiparqueo"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }
        val mapsApiKey = localProperties.getProperty("MAPS_API_KEY") ?: ""
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.all {
            // Avoid bytecode verification issues when coverage agent touches android-all classes
            it.jvmArgs("-noverify")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.glide)
    implementation(libs.retrofit)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.koin.android)
    implementation (libs.koin.androidx.navigation)
    implementation (libs.koin.androidx.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.converter.gson)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.bom)

    // Google Maps para Jetpack Compose
    // Maps Compose
    implementation("com.google.maps.android:maps-compose:4.3.3")
    // Optional: Utilities for clustering, etc.
    implementation("com.google.maps.android:maps-compose-utils:4.3.3")
    // Optional: Widgets like ScaleBar
    implementation("com.google.maps.android:maps-compose-widgets:4.3.3")
    //LOCALIZACION
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    //local bundle room
    implementation(libs.bundles.local)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.remoteconfig)
    implementation(libs.datastore)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)
    testImplementation(libs.room.testing)
    coreLibraryDesugaring(libs.android.desugar.jdk)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.androidx.ui.test.manifest)
    testImplementation("androidx.test:core:1.6.1")
    testImplementation("org.robolectric:robolectric:4.12.1")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Restrict Jacoco to instrument only our code to keep Robolectric happy during coverage runs
tasks.withType<Test>().configureEach {
    jacoco {
        excludes += listOf(
            "jdk.internal.*",
            "java.*",
            "sun.*",
            "android.*",
            "androidx.*",
            "org.robolectric.*",
            "kotlin.*",
            "kotlinx.*",
            "com.google.android.*"
        )
    }
}
