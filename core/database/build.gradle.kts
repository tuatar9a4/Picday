plugins {
    id("devd.android.library")
    id("devd.android.room")
}

android {
    namespace = "com.devd.database"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(projects.core.model)
}