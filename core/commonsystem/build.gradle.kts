plugins {
    id("devd.android.library")
}

android {
    namespace = "com.devd.commonsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    

}