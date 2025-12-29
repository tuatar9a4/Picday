import com.devd.build_logic.app.configTimberLogger
import com.devd.build_logic.app.configureDaggerHilt
import com.devd.build_logic.app.configureKotlinAndroid

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

configureKotlinAndroid()
configureDaggerHilt()
configTimberLogger()