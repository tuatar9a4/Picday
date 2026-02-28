plugins {
    id("devd.android.feature")
}

android {
    namespace = "com.devd.diary"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

}