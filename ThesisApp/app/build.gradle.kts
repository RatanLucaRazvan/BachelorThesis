plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
}

android {
    namespace = "com.example.thesisapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.thesisapp"
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
    packagingOptions {
        resources {
            pickFirsts.add("META-INF/LICENSE.md") // ADD THIS LINE
            pickFirsts.add("META-INF/LICENSE-notice.md") // ADD THIS LINE
            // ... keep other pickFirsts you added previously (e.g., META-INF/AL2.0, META-INF/LGPL2.1 etc.)
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.androidx.junit.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.core.testing)
    //noinspection UseTomlInstead
    testImplementation("io.mockk:mockk:1.13.11") // Or org.mockito:mockito-core:4.11.0 if you prefer Mockito
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.core.testing)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom.v20240400)) // Use your current Compose BOM version
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

}