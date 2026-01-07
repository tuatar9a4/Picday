plugins {
    id("devd.android.feature")
}

android {
    namespace = "com.devd.editor"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(projects.core.commonsystem)
}