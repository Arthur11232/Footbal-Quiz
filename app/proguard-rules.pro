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

# Navigation/FragmentContainerView instantiate fragments by class name
# from the navigation graph at runtime, so fragment classes must survive R8.
-keep class * extends androidx.fragment.app.Fragment
-keepclassmembers class * extends androidx.fragment.app.Fragment {
    public <init>();
}

# Keep custom views inflated from XML.
-keep class * extends android.view.View
-keepclassmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep activities and application class names used by Android framework.
-keep class * extends android.app.Activity
-keep class * extends android.app.Application

# Keep NavGraph argument classes referenced by fully-qualified name in XML.
-keep class com.arthuralexandryan.footballquiz.models.GameObjectSerializable { *; }
-keep class com.arthuralexandryan.footballquiz.models.GameObjectScores { *; }
