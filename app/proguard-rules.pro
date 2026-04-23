# فایل: app/proguard-rules.pro
-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlin.** { *; }
-keep class com.example.wifiscanner.** { *; }
-dontwarn org.jetbrains.**
