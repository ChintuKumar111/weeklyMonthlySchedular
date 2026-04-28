// C:/Users/Admin/AndroidStudioProjects/simpleApiCalling/build.gradle.kts

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    // Add this line
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
}