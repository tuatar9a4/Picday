import com.devd.build_logic.app.configureFirebaseConsole

plugins {
    id("devd.android.library")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.devd.firebase"
}

configureFirebaseConsole()

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    implementation(platform(libs.google.firebase.bom))
//    implementation(libs.google.firebase.cloud.message)
    implementation(projects.core.datastore)
}