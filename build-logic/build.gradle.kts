plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
}


gradlePlugin{
    plugins {
        register("androidRoomDB"){
            id ="vueroid.android.room"
            implementationClass = "com.devd.build_logic.app.AndroidRoomPlugin"
        }
        register("androidRetrofit"){
            id ="vueroid.android.retrofit"
            implementationClass = "com.devd.build_logic.app.AndroidRetrofitPlugin"
        }
    }
}