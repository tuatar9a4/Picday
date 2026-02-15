import com.devd.build_logic.app.configTimberLogger
import com.devd.build_logic.app.configureDaggerHilt
import com.devd.build_logic.app.configureKotlinAndroid
import com.devd.build_logic.app.libs

plugins {
    id("devd.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
}

android{
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}



configureKotlinAndroid()
configureDaggerHilt()
configTimberLogger()

dependencies{
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:datastore"))
    implementation(project(":core:commonsystem"))


    val libs = project.extensions.libs
    implementation(libs.findLibrary("androidx.appcompat").get())
    implementation(libs.findLibrary("material").get())
    testImplementation(libs.findLibrary("junit").get())
    androidTestImplementation(libs.findLibrary("androidx.junit").get())
    androidTestImplementation(libs.findLibrary("androidx.espresso.core").get())

//    lifecycle
    implementation(libs.findLibrary("androidx.lifecycle.livedata").get())
    implementation(libs.findLibrary("androidx.lifecycle.viewmodel").get())

//    navigation
    implementation(libs.findBundle("androidx.navigation.bundle").get())

    implementation(platform(libs.findLibrary("androidx.compose.bom").get()))
    implementation(libs.findLibrary("androidx.compose.ui").get())
    implementation(libs.findLibrary("androidx.compose.ui.graphics").get())
    implementation(libs.findLibrary("androidx.compose.ui.tooling.preview").get())
    implementation(libs.findLibrary("androidx.compose.material3").get())
    implementation(libs.findLibrary("androidx.activity.compose").get())
    implementation(libs.findLibrary("androidx.compose.material3.adaptive").get())
    implementation(libs.findLibrary("androidx.compose.foundation").get())
    implementation(libs.findLibrary("androidx.lifecycle.viewmodel.compose").get())


    debugImplementation(libs.findLibrary("androidx.compose.ui.tooling.preview").get())
    debugImplementation(libs.findLibrary("androidx.compose.ui.tooling").get())

//    Coil
    debugImplementation(libs.findLibrary("coil.image.compose").get())
    debugImplementation(libs.findLibrary("coil.http.compose").get())


}