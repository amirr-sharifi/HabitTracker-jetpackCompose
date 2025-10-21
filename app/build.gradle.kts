plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)

}

android {
    namespace = "af.amir.mytasky"
    compileSdk = 35

    defaultConfig {
        applicationId = "af.amir.mytasky"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemeLocation"] = "$projectDir/schemas"
            }
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    /*composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }*/
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.material.icons.core.android)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.ui.test.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.material.icons.extended)

    implementation("com.google.android.material:material:1.12.0")
    val lifecycle_version = "2.9.1"
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // Hilt dependencies
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)



    //room
    val room_version = "2.7.2"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")


    //Gson
    implementation("com.google.code.gson:gson:2.13.1")

    //ThreeTenABP
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.9")


    //navigation
    val nav_version = "2.9.1"
    implementation("androidx.navigation:navigation-compose:$nav_version")

    //Persian date
    implementation("com.github.samanzamani:PersianDate:1.7.1")

    //Persian date picker
    implementation("dev.chrisbanes.snapper:snapper:0.3.0")

    //Lottie
    implementation("com.airbnb.android:lottie-compose:6.6.2")

    //DateStore
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.core:core-splashscreen:1.1.0-rc01")

   }