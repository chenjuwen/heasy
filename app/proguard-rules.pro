#指定代码的压缩级别
-optimizationpasses 5
 #优化  不优化输入的类文件
-dontoptimize
#不做预校验
-dontpreverify
 #混淆时是否记录日志
-verbose
 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*




#保留不混淆:注解、内部类、泛型
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

#抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 保留四大组件，自定义的Application等
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment

#如果引用了v4或者v7包
-dontwarn android.support.**

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# 保留自定义View
-keepclassmembers public class * extends android.view.View{
    void set*(***);
    *** get*();
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

#保持 Parcelable 不被混淆
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

#保留R文件中的成员
-keepclassmembers class **.R$* {
    public static <fields>;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# webView处理
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}



#EventBus
#-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#保证第三方依赖包不混淆
-dontwarn org.**
-keep class org.** { *; }

-dontwarn net.**
-keep class net.** { *; }

-dontwarn de.**
-keep class de.** { *; }

-dontwarn ch.**
-keep class ch.** { *; }

-dontwarn java.**
-keep class java.** { *; }

-dontwarn javax.**
-keep class javax.** { *; }

-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

-dontwarn okio.**
-keep class okio.** { *; }

-dontwarn com.googlecode.**
-keep class com.googlecode.** { *; }

-dontwarn com.google.**
-keep class com.google.** { *; }

-dontwarn com.alibaba.**
-keep class com.alibaba.** { *; }

-dontwarn com.howardreactive.**
-keep class com.howardreactive.** { *; }

-dontwarn com.soundcloud.**
-keep class com.soundcloud.** { *; }

-dontwarn com.journeyapps.**
-keep class com.journeyapps.** { *; }

-dontwarn com.heasy.**
-keep class com.heasy.** { *; }

#OCR
-keep class com.baidu.ocr.sdk.**{*;}
-dontwarn com.baidu.ocr.**

#TTS
-keep class com.baidu.tts.**{*;}
-keep class com.baidu.speechsynthesizer.**{*;}

#Speech
-keep class com.baidu.speech.**{*;}
