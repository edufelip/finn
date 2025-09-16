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
- Room (ready for local persistence)
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

Build/Run
- Sync and run from Android Studio or:
  - `./gradlew :app:assembleDebug`
  - Lint/Checks: `./gradlew ktlintCheck detekt :app:lintDebug`
  - Resource usage report: `./gradlew :app:resourceUsageReport` (see `app/build/reports/lint-results-debug.html`)

Notifications
- Android uses FCM. Foreground notifications are shown, and a Flow-based repository feeds the UI.
- iOS scaffold included: call `IosPush.requestAuthorization()` at startup and `IosPush.handleRemoteNotification(title, body)` from your AppDelegate to feed shared UI.

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
