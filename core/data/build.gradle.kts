plugins {
    id("devd.android.library")
    id("devd.android.retrofit")
    id("devd.android.room")
}

android {
    namespace = "com.devd.data"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.model)

}