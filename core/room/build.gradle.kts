plugins {
    id("devd.android.library")
    id("devd.android.room")
}

android {
    namespace = "com.devd.room"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}