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


### ADDED ###
# Rules added to the existing keep rules in order to suppress warnings when generating APK.
-dontwarn android.content.pm.PackageManager$ApplicationInfoFlags*
-dontwarn android.content.pm.PackageManager$PackageInfoFlags*


### ADDED ###
# Keep rules for excluding models from obfuscation

# Largest scope...
# -keep class com.example.go4lunch.model.** { *; }

# Intermediate scope...
# -keep class com.example.go4lunch.model.api.** { *; }
# -keep class com.example.go4lunch.model.model.** { *; }

# Most restricted (and best) scope (only models)
-keep class com.example.go4lunch.model.api.GmapsRestaurantPojo { *; }
-keep class com.example.go4lunch.model.api.GmapsRestaurantDetailsPojo { *; }
-keep class com.example.go4lunch.model.api.model.** { *; }
-keep class com.example.go4lunch.model.model.Restaurant { *; }
-keep class com.example.go4lunch.model.model.User { *; }
-keep class com.example.go4lunch.model.model.LikedRestaurant { *; }


### ADDED ###
# Keep rules for keeping generic signature of Retrofit2 return types (#3886)

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not kept.
# Suspend functions are wrapped in continuations where the type argument is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
