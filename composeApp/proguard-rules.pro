-keep class org.webrtc.** { *; }
-dontwarn org.webrtc.**
-keep class io.github.webrtc_sdk.** { *; }
-dontwarn io.github.webrtc_sdk.**
-dontwarn org.webrtc.voiceengine.WebRtcAudioTrack
-dontwarn android.media.AudioTrack
-keep class * implements org.webrtc.PeerConnectionClient { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepclassmembers class * {
    @org.webrtc.* *;
}