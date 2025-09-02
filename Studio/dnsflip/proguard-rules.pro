# DNSFlip ProGuard Rules
# Optimized for release builds with proper obfuscation

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep DNSFlip core classes
-keep class com.mjryan253.dnsflip.DNSManager { *; }
-keep class com.mjryan253.dnsflip.PreferencesManager { *; }
-keep class com.mjryan253.dnsflip.ShizukuManager { *; }

# Keep Shizuku API classes
-keep class dev.rikka.shizuku.** { *; }
-dontwarn dev.rikka.shizuku.**

# Keep Compose classes
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Material3 classes
-keep class androidx.compose.material3.** { *; }

# Keep AndroidX classes
-keep class androidx.** { *; }
-dontwarn androidx.**

# Keep reflection-based classes
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Optimize
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify