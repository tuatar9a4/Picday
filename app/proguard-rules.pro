# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepattributes *Annotation*, Signature, InnerClasses

-keep class com.devd.model.** { *; }
-keep class com.devd.room.** { *; }

-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keep class androidx.exifinterface.media.ExifInterface { *; }

# DataModel을 상속받는 모든 클래스와 그 멤버를 보존
-keep class * implements com.devd.model.local.NavRoute { *; }