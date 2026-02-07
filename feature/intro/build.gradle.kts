plugins {
    id("devd.android.feature")
}

android {
    namespace = "com.devd.intro"

    buildFeatures{
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(projects.core.commonsystem)
    implementation(projects.core.datastore)
    implementation(projects.core.data)
}