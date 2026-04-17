# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt

# Keep serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.spbchurch.radio.**$$serializer { *; }
-keepclassmembers class com.spbchurch.radio.** {
    *** Companion;
}
-keepclasseswithmembers class com.spbchurch.radio.** {
    kotlinx.serialization.KSerializer serializer(...);
}
