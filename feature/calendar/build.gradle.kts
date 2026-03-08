plugins {
    id("devd.android.feature")
}

android {
    namespace = "com.devd.calendar"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}