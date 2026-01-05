plugins {
    id("devd.android.library")
    id("devd.android.retrofit")
}

android {
    namespace = "com.devd.network"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

}