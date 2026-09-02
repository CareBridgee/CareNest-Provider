# Add project specific ProGuard rules here.

# Keep standard Kotlin & Java attributes required for reflection, metadata, and stacktraces
-keepattributes Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable,*Annotation*,RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeVisibleTypeAnnotations
-keepclassmembers class * {
    ** Companion;
}
-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-dontwarn kotlin.**

# Kotlinx Serialization
-keepattributes *Annotation*,ElementValueAttribute
-keep @"kotlinx.serialization.Serializable" class * { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
    @kotlinx.serialization.SerialName *;
}
-keepclassmembers class * {
    public static final ** Companion;
}
-keepclassmembers class * extends kotlinx.serialization.KSerializer {
    public static final ** INSTANCE;
}
-keep class com.carenest.provider.**$serializer { *; }
-keep class com.carenest.**$serializer { *; }

# Hilt & Dagger Dependency Injection
-keep class * extends android.app.Application
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keepclassmembers class * {
    @javax.inject.Inject *;
    @jakarta.inject.Inject *;
}
-keep @dagger.hilt.MigrationEntryPoint class *
-keep @dagger.hilt.EntryPoint class *
-keep @dagger.hilt.InstallIn class *
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keep @dagger.hilt.android.HiltAndroidApp class *
-keep class **_HiltModules* { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }
-dontwarn dagger.hilt.internal.**

# Ktor Client, OkHttp & STOMP WebSockets
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class com.carenest.provider.core.network.socket.stomp.** { *; }

# Mapbox SDK
-keep class com.mapbox.** { *; }
-dontwarn com.mapbox.**

# CameraX & ML Kit Barcode Scanning
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# Coil Image Loader & Lottie Animations
-keep class io.coil_kt.** { *; }
-dontwarn io.coil_kt.**
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Jetpack Compose & Navigation 3
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keep class androidx.navigation3.** { *; }
-dontwarn androidx.navigation3.**

# DataStore Preferences
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# CareNest Provider Domain, DTOs, MVI Contracts, and Data Models
-keep class com.carenest.provider.core.model.** { *; }
-keep class com.carenest.**.domain.model.** { *; }
-keep class com.carenest.**.domain.usecase.** { *; }
-keep class com.carenest.**.data.dto.** { *; }
-keep class com.carenest.**.data.remote.** { *; }
-keep class com.carenest.**.presentation.**Contract* { *; }
