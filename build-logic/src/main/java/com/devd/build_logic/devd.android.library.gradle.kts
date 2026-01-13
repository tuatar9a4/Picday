import com.devd.build_logic.app.configTimberLogger
import com.devd.build_logic.app.configureDaggerHilt
import com.devd.build_logic.app.configureKotlinAndroid
import com.devd.build_logic.app.libs

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

configureKotlinAndroid()
configureDaggerHilt()
configTimberLogger()

dependencies{
    val libs = project.extensions.libs
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