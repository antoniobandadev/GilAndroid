plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android")
    id ("kotlin-kapt")
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.jbg.gil"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jbg.gil"
        minSdk = 24
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
        viewBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    //Splash
    implementation(libs.androidx.core.splashscreen)

    //NavComponent
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.animation.core.android)

    //Retrofit ,Gson e Interceptor
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    //ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    //LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:2.8.7")
    //Inst viewModel Fragments
    implementation("androidx.fragment:fragment-ktx:1.8.6")
    //Inst ViewModel Activities
    implementation("androidx.activity:activity-ktx:1.10.1")

    //DataStore persistent simpleData
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    //Room
    implementation ("androidx.room:room-runtime:2.5.0")
    implementation ("androidx.room:room-ktx:2.5.0")
    ksp ("androidx.room:room-compiler:2.5.0")
    //ksp(libs.androidx.room.compiler)

    //Hilt
    implementation("com.google.dagger:hilt-android:2.56.2")
    kapt("com.google.dagger:hilt-android-compiler:2.56.2")

    //Shimmer
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    //ImagePicker
    implementation ("androidx.activity:activity:1.6.1")

    //SyncTask
    implementation ("androidx.work:work-runtime-ktx:2.9.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.13.2")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}