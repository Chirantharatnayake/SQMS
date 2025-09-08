plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services") // Ensure Google Services is applied
}

android {
    namespace = "com.example.queuemanagmentsystem"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.queuemanagmentsystem"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Define the API key using manifestPlaceholders
        manifestPlaceholders += mapOf(
            "google_maps_key" to "AIzaSyDixkE6Z6eoaB6dEwZtJvLdMRq2AchlLmA" // Your Google Maps API Key
        )
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
    }

    kotlinOptions {
        jvmTarget = "11"
        // To smooth over prerelease mismatches (harmless)
        freeCompilerArgs += "-Xskip-prerelease-check"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {
    // AndroidX + Compose BOM
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Material Components for custom corner sizes, etc.
    implementation("com.google.android.material:material:1.12.0")

    // Firebase (BoM-based)
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Google Maps Compose for Kotlin 2.0.x compatibility
    implementation("com.google.maps.android:maps-compose:6.4.0")

    // Explicit maps dependency (useful for ensuring compatibility)
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Places SDK (new)
    implementation("com.google.android.libraries.places:places:4.3.1")

    // Fused Location Provider for location services
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Coroutines for asynchronous tasks
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    // Needed for .await() on Tasks (Places, FusedLocation)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // Miscellaneous utilities for Compose
    implementation("androidx.compose.material:material-icons-extended")
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Force Kotlin stdlib version 2.0.x to avoid accidental usage of 2.2.0
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.21")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
    }

    // Unit and Instrumentation Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
