plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"
}

android {
    namespace = "sk.duracik.myaiapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "sk.duracik.myaiapplication"
        minSdk = 29
        targetSdk = 35
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // WorkManager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    implementation(libs.androidx.material3)

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Novšia Compose Pager knižnica namiesto zastaralej Accompanist
    implementation("androidx.compose.foundation:foundation:1.6.0")

    // Material Icons Extended - rozšírená sada ikon
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    // Accompanist permissions pre jednoduchšiu správu povolení v Compose
    implementation("com.google.accompanist:accompanist-permissions:0.33.2-alpha")
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.work.runtime.ktx)

    // Room database
    val roomVersion = "2.7.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")  // Kotlin Extensions a Coroutines support pre Room
    ksp("androidx.room:room-compiler:$roomVersion") // Generátor kódu pre Room

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
