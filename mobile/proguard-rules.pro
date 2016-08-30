# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /media/additional/android-sdks.20150505/android-sdks/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#ABC
# v7.** get obfuscated. eg. v7.internal.** and v7.widget.** cause errors if obfuscated.
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep class !android.support.v7.internal.view.menu.**,android.support.** {*;}

#Design support widgets
-keep class android.support.design.** { *; }

#To revise
-keep class com.google.android.gms.** { *; }
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }


#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

#Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#To remove debug logs:
-assumenosideeffects class android.util.Log {
public static *** d(...);
public static *** v(...);
}

#To keep line numbers for crash reports
-renamesourcefileattribute SourceFile
-keepattributes SourceFile, LineNumberTable

#firebase
# Add this global rule
-keepattributes Signature

# This rule will properly ProGuard all the model classes
-keepclassmembers class com.tinashe.guestlist.model.** {
  *;
}