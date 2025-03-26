plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.carnotificationsapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.carnotificationsapp"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation(libs.gson)

    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    implementation(libs.core.ktx)
    implementation(libs.work.runtime.ktx)
    implementation(libs.work.runtime)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}