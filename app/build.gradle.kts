plugins {
    id("devd.android.application")
}

android {
    namespace = "com.devd.onedayoneshot"

    defaultConfig {
        applicationId = "com.devd.onedayoneshot"
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.bundles.androidx.navigation.bundle)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation(projects.core.commonsystem)
    implementation(projects.core.datastore)
    implementation(projects.core.model)
    implementation(projects.feature.intro)
    implementation(projects.feature.user)
    implementation(projects.feature.home)
    implementation(projects.feature.editor)
    implementation(projects.feature.diary)
    implementation(projects.feature.calendar)
    implementation(projects.feature.bookcase)
    implementation(projects.feature.setting)

}