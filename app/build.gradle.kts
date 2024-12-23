plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.smartcents"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.smartcents"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.tools.core)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")




    // Firebase platform (BoM) for managing dependency versions
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // MPAndroidChart for charting
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // FirebaseUI for Firebase Auth
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")

    // FirebaseUI for Firestore
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")

    // Protobuf Javalite (ensures compatibility)
    implementation("com.google.protobuf:protobuf-javalite:3.21.12")
}

// Exclude duplicate protobuf dependencies to resolve conflicts
configurations.all {
    exclude(group = "com.google.protobuf", module = "protobuf-java")
    resolutionStrategy.force("com.google.protobuf:protobuf-javalite:3.21.12")
}
