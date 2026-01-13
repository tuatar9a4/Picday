
plugins {
    id("devd.android.library")
}

android {
    namespace = "com.devd.commonsystem"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    //NOTE : CommonSystem 의 경우 UI를 추가하는 경우도 있으니 Compose 추가
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.compose.ui)
//    implementation(libs.androidx.compose.ui.graphics)
//    implementation(libs.androidx.compose.ui.tooling.preview)
//    implementation(libs.androidx.compose.material3)
//    implementation(libs.androidx.activity.compose)
//    implementation(libs.androidx.compose.material3.adaptive)
//    implementation(libs.androidx.compose.foundation)
//    implementation(libs.androidx.lifecycle.viewmodel.compose)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
//    debugImplementation(libs.androidx.compose.ui.tooling)
//    debugImplementation(libs.androidx.compose.ui.test.manifest)
}