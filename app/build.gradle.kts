plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization) // Add this line
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.weatherapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
    buildFeatures {
        compose = true
    }
    viewBinding {
        enable = true
    }
    dataBinding {
        enable = true
    }
}

dependencies {
    // Retrofit for network calls
    implementation("com.squareup.moshi:moshi:1.13.0") // Core Moshi library
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0") // Moshi Kotlin adapter
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit dependency
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0") // Retrofit converter for Moshi

    // UI and Material Design
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    // Jetpack Compose and AndroidX libraries
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.ui:ui-graphics:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Unit Testing
    testImplementation("junit:junit:4.13.2") // JUnit 4
    testImplementation("org.mockito:mockito-core:5.5.0") // Mockito Core
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0") // Mockito Kotlin
    testImplementation("androidx.arch.core:core-testing:2.2.0") // LiveData testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") // Coroutines test

    // Android Instrumentation Tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // Android JUnit extensions
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // Espresso
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0") // Compose testing
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.0")
}