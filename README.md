## TrustMeAlready
An Xposed module to disable SSL verification and pinning on Android using the excellent technique provided by [Mattia Vinci](https://techblog.mediaservice.net/2018/11/universal-android-ssl-check-bypass-2/).

### Requirements
* Android 8.0+ (`minSdk 26`).
* A modern Xposed framework supporting [libxposed API](https://github.com/libxposed/api) 101+ (e.g., LSPosed with modern Xposed API support).
* Enable the module for the target applications in your Xposed manager scope.

### Build
* Requires JDK 17+ and Android SDK 36.
* Dependencies: `io.github.libxposed:api:102.0.0` (configured as `compileOnly`).

### Troubleshooting
* Some apps implement custom certificate checking, bypassing this hook. Try sniffing Chrome traffic; if you don't get an invalid certificate error then this module is working as it should.
* Check your Xposed logs; chances are this module couldn't hook the correct method or wasn't enabled in the target app's scope.
