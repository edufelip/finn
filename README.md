<h1 align="center">Finn</h1>

<p align="center">
  <a href="https://android-arsenal.com/api?level=24"><img alt="API" src="https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/edufelip"><img alt="Build Status" src="https://img.shields.io/static/v1?label=Android+CI&message=passing&color=green&logo=android"/></a>
  <a href="https://medium.com/@eduardofelipi"><img alt="Medium" src="https://img.shields.io/static/v1?label=Medium&message=@edu_santos&color=gray&logo=medium"/></a> <br>
  <a href="https://www.youtube.com/channel/UCYcwwX7nDU_U0FP-TsXMwVg"><img alt="Profile" src="https://img.shields.io/static/v1?label=Youtube&message=edu_santos&color=red&logo=youtube"/></a> 
  <a href="https://github.com/edufelip"><img alt="Profile" src="https://img.shields.io/static/v1?label=Github&message=edufelip&color=white&logo=github"/></a> 
  <a href="https://www.linkedin.com/in/eduardo-felipe-dev/"><img alt="Linkedin" src="https://img.shields.io/static/v1?label=Linkedin&message=edu_santos&color=blue&logo=linkedin"/></a> 
</p>

<p align="center">  
🗡️ Finn is a social media that let you create communities and post about the things you love!
</p>

<p align="center">
<img src="https://github.com/edufelip/finn/assets/34727187/f55b5475-8718-4891-a468-8896ec113a2e"/>
</p>

## Download
Go to [Google Play](https://play.google.com/store/apps/details?id=com.edufelip.finn) to download the latest App version.

## Tech Stack (modernized)
- Kotlin + Coroutines/Flow
- Jetpack Compose Multiplatform (shared UI for Android/iOS)
- Clean Architecture (domain/data/presentation)
- Hilt for DI
- Retrofit + OkHttp
- Room Multiplatform (shared cache with generated queries for Android/iOS)
- Firebase (Auth, Remote Config, Messaging/FCM)
- Navigation Compose, Coil (Android), Kamel (iOS)
- Lint, ktlint, detekt, GitHub Actions CI

## Setup
Clone this repository and open in **Android Studio** (Giraffe+)
```bash
git clone https://github.com/edufelip/finn.git
```
or

```bash
git clone git@github.com:edufelip/finn.git
```

Requirements
- JDK 17, Android SDK 34
- `app/google-services.json` from your Firebase project
- `local.properties` should contain `FIREBASE_GOOGLE_ID=YOUR_GOOGLE_CLIENT_ID`
- The project resolves iOS image loading via Kamel and shared persistence via Room (KMP); keep the JetBrains Space repository (`https://maven.pkg.jetbrains.space/public/p/ktor/eap`) in `settings.gradle.kts` if you customize repositories.

Build/Run
- Sync and run from Android Studio or:
  - `./gradlew :app:assembleDebug`
  - Lint/Checks: `./gradlew ktlintCheck detekt :app:lintDebug`
  - Resource usage report: `./gradlew :app:resourceUsageReport` (see `app/build/reports/lint-results-debug.html`)
  - All-in-one verification (formatting, unit tests, Room schema export): `./gradlew ci` (run `./gradlew :app:lintDebug` separately if you need full lint coverage)

iOS
- Refresh the shared Compose framework + trigger an Xcode build: `./scripts/rebuild_ios.sh` (set `CONFIG=Release` and `SDK_NAME=iphoneos` when archiving)
- The generated framework lives under `composeApp/build/xcode-frameworks/<CONFIG>/<SDK>` and is synced into `iosApp/Frameworks/`.

Notifications
- Android uses FCM. Foreground notifications are shown, and a Flow-based repository feeds the UI.
- iOS scaffold included: call `IosPush.requestAuthorization()` at startup and `IosPush.handleRemoteNotification(title, body)` from your AppDelegate to feed shared UI.

## Remote Config
- The app expects the following parameters in Firebase Remote Config (defaults live in `app/src/main/res/xml/remote_config_defaults.xml`):
  - `feed_cache_ttl_ms`
  - `community_search_ttl_ms`
  - `community_details_ttl_ms`
  - `comment_cache_ttl_ms`
- These values (in milliseconds) control how long feed, community, and comment responses stay in the Room-backed cache before a fresh network fetch is enforced.
- Quick setup:
  1. Open your Firebase project → Remote Config → Add parameters matching the keys above.
  2. Use the defaults from `remote_config_defaults.xml` (5–15 minutes) or adjust per environment.
  3. Publish the Remote Config changes, then restart the app (debug fetch interval is 1 s, release 1 h).
- You can also override `comments_page_size` and `remote_server` the same way.
- The default backend host is `https://finn.dev.dashboard.eduwaldo.com/` for debug builds and `https://finn.dashboard.eduwaldo.com/` for release builds. Android derives this from `BuildConfig.DEFAULT_REMOTE_SERVER`, iOS from `FinnRemoteServer` in `iosApp/iosApp/Info.plist`. Remote Config values override these defaults at runtime, but must remain HTTPS URLs with a trailing slash.
- On iOS, `IosRemoteConfigSync` polls `https://finn.dev.dashboard.eduwaldo.com/mobile-config/ios.json` during startup and persists any returned values inside `NSUserDefaults`. Populate that endpoint (or set `FinnRemoteServer` in your xcconfig) to keep Remote Config parity with Android.
- The default backend host is `https://finn.dev.dashboard.eduwaldo.com/`. Any override must use HTTPS and include the trailing slash so Retrofit can build the client.

## Architecture Notes
- Domain use cases emit `Flow<Result<T>>`, so UI layers collect a single stream for loading, success, and error states without manual try/catch.
- Network calls use an OkHttp `HttpLoggingInterceptor` wired to `FinnNetwork` for verbose request/response logs in debug builds.


## Modules
- `app/` Android application (Hilt wiring, platform services, Navigation Compose)
- `shared/` Compose Multiplatform UI + domain/data contracts and presentation

## Generating APK
From Android Studio:
1. ***Build*** menu
2. Generate ***Bundle(s) / APK(s)***
3. Build ***APK(s)***
4. Wait for Android studio to build the APK

## Maintainers
This project is mantained by:
* [Eduardo Felipe](http://github.com/edufelip)

## Contributing

1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -m 'Add some feature')
4. Push your branch (git push origin my-new-feature)
5. Create a new Pull Request
