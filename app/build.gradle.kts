plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.moutamid.sprachelernenadmin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.moutamid.sprachelernenadmin"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        setProperty("archivesBaseName", "SpracheLernenAdmin-$versionName")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { viewBinding = true }

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
//    implementation("com.fxn769:stash:1.3.2")
    implementation("com.github.dhaval2404:imagepicker:2.1")

    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-crashlytics:18.6.0")
    implementation("com.google.firebase:firebase-analytics:21.5.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.9.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.code.gson:gson:2.8.2")
}