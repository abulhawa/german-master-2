# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\user\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep rules here:

# ML Kit Translation rules
-keep class com.google.mlkit.common.internal.CommonComponentRegistrar { <init>(); }
-keep class com.google.mlkit.nl.translate.NaturalLanguageTranslateRegistrar { <init>(); }
-keep class com.google.mlkit.nl.translate.** { *; }

# If your project uses WebView with JS, uncomment the following:
# -keepclassmembers class fqcn.of.javascript.interface.for.webview {
#    public *;
# }

# Uncomment this to preserve the line number information for
# debugging stack traces.
# -keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
# -renamesourcefileattribute SourceFile
