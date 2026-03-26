plugins {
    id("devd.android.library")
}

android {
    namespace = "com.devd.datastore"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(projects.core.model)
}