import com.devd.build_logic.app.configureFirebaseConsole
import java.util.Properties

plugins {
    id("devd.android.application")
    id("com.google.gms.google-services")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "com.devd.picday"
    defaultConfig {
        targetSdk = 36
        applicationId = "com.devd.picday"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release"){
            storeFile = localProperties.getProperty("KEY_PATH")?.let { file(it) }
            storePassword = localProperties.getProperty("STORE_PASSWORD")
            keyAlias = localProperties.getProperty("KEY_ALIAS")
            keyPassword = localProperties.getProperty("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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

configureFirebaseConsole()

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
    implementation(projects.core.firebase)
    implementation(projects.feature.intro)
    implementation(projects.feature.user)
    implementation(projects.feature.home)
    implementation(projects.feature.editor)
    implementation(projects.feature.diary)
    implementation(projects.feature.calendar)
    implementation(projects.feature.bookcase)
    implementation(projects.feature.setting)

}