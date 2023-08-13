plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "id.psw.fontview"
    compileSdk = 33

    defaultConfig {
        applicationId = "id.psw.fontview"
        minSdk = 19
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation("com.github.dcsch:typecast:master-SNAPSHOT")
}