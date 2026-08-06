plugins { alias(libs.plugins.android.application); alias(libs.plugins.kotlin.android); alias(libs.plugins.kotlin.compose); alias(libs.plugins.ksp); alias(libs.plugins.hilt) }

android {
    namespace = "com.misaventuras"; compileSdk = 35
    defaultConfig { applicationId = "com.misaventuras"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "1.0"; testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { compose = true; buildConfig = true }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

// Keep Javac, Kotlin and KSP on the same bytecode target regardless of the JDK
// Android Studio itself uses to launch Gradle.
kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(platform(libs.compose.bom)); androidTestImplementation(platform(libs.compose.bom))
    implementation(libs.compose.ui); implementation(libs.compose.material3); implementation(libs.compose.icons); implementation(libs.compose.ui.tooling.preview)
    implementation(libs.activity.compose); implementation(libs.navigation.compose); implementation(libs.lifecycle.runtime.compose); implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.room.runtime); implementation(libs.room.ktx); ksp(libs.room.compiler)
    implementation(libs.datastore); implementation(libs.hilt.android); ksp(libs.hilt.compiler); implementation(libs.hilt.navigation)
    testImplementation(libs.junit); testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.androidx.junit); androidTestImplementation(libs.espresso); androidTestImplementation(libs.compose.test)
    debugImplementation(libs.compose.ui.tooling); debugImplementation(libs.compose.test.manifest)
}
