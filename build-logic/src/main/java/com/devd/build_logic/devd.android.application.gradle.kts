import com.devd.build_logic.app.configTimberLogger
import com.devd.build_logic.app.configureDaggerHilt
import com.devd.build_logic.app.configureKotlinAndroid
import com.devd.build_logic.app.libs

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
}

configureKotlinAndroid()
//configureSigningAndroid()
configureDaggerHilt()
configTimberLogger()

dependencies{
    val libs = project.extensions.libs
    //    navigation
    "implementation"(libs.findBundle("androidx.navigation.bundle").get())
}