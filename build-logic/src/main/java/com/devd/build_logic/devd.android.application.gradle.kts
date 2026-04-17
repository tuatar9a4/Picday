import com.devd.build_logic.app.configTimberLogger
import com.devd.build_logic.app.configureDaggerHilt
import com.devd.build_logic.app.configureKotlinAndroid
import com.devd.build_logic.app.libs
import gradle.kotlin.dsl.accessors._7a04235ccec5a434c2b9b6f12fbf7fd9.debugImplementation
import gradle.kotlin.dsl.accessors._7a04235ccec5a434c2b9b6f12fbf7fd9.implementation

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
//    id("com.google.gms.google-services")
}

configureKotlinAndroid()
//configureSigningAndroid()
configureDaggerHilt()
configTimberLogger()

dependencies{
    val libs = project.extensions.libs
    //    navigation
    "implementation"(libs.findBundle("androidx.navigation.bundle").get())



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
}