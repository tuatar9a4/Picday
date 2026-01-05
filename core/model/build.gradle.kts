plugins {
    id("devd.android.library")
}

android {
    namespace = "com.devd.model"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}