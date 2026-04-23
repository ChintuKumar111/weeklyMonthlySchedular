plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.parcelize)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.shyamdairyfarm.user"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.shyamdairyfarm.user"
        minSdk = 24
        targetSdk = 36
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

    buildFeatures {
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    androidResources {
        noCompress += "lottie"
    }
}

dependencies {

    // Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation("com.google.android.material:material:1.12.0")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation(libs.play.services.analytics.impl)
    implementation(libs.play.services.maps3d)

    // Navigation Component (Fragments)
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

   //  Firebase
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation(libs.firebase.storage.ktx)
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation("com.google.firebase:firebase-appcheck-debug")

    // Google Play Services for SMS Retriever API
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.android.gms:play-services-auth-api-phone:18.1.0")


    // UI Utils
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")
    implementation(libs.circleimageview)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // lottie animation
    implementation("com.airbnb.android:lottie:6.7.1")


    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // For loading images in Compose
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    // For network image loading
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    // taptargetView
    implementation("com.getkeepsafe.taptargetview:taptargetview:1.13.3")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    // Koin for Android
    implementation("io.insert-koin:koin-android:3.5.3")

    // Dots Indicator
    implementation("com.tbuonomo:dotsindicator:5.1.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    // razor pay================
    implementation("com.razorpay:checkout:1.6.33")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // zoom out and zoom in functionality
    implementation("com.otaliastudios:zoomlayout:1.9.0")

    // dot indicator
    implementation("com.tbuonomo:dotsindicator:5.1.0")

    // otp box
    implementation("io.github.chaosleung:pinview:1.4.4")

    // Media3 ExoPlayer for Video support
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-ui:1.5.1")
    implementation("androidx.media3:media3-common:1.5.1")
    // bottom navigation
//    implementation("me.ibrahimsn:smoothbottombar:1.7.9")
    implementation("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")
}