# iOS App Module

The `iosApp/` directory packages the Xcode sources that embed the shared Compose Multiplatform UI exported from the `composeApp` module.

## Layout

- `iosApp/iosApp.xcodeproj` – Xcode project referencing the generated `FinnApp.framework`.
- `iosApp/iosApp` – Swift entry point (`iOSApp.swift`) and SwiftUI bridge (`ContentView.swift`).
- `iosApp/Configuration/Config.xcconfig` – Environment toggles (team ID, bundle ID, framework location).
- `iosApp/build.gradle.kts` – Helper Gradle tasks to build/sync the Compose framework and optionally launch Xcode.

## Building the Framework

```bash
./gradlew :iosApp:syncFramework
```

This delegates to `:composeApp:embedAndSignAppleFrameworkForXcode` and copies the fat framework under `composeApp/build/xcode-frameworks/<Config>/<SDK>` where the Xcode project expects it.

## Opening the Project in Xcode (macOS)

```bash
./gradlew :iosApp:openXcode
```

The task ensures the framework is up to date before launching Xcode. On non-macOS environments the task is skipped.

## Manual Steps After Cloning

1. Set your Apple developer `TEAM_ID` and desired `BUNDLE_ID` inside `iosApp/Configuration/Config.xcconfig`.
2. Run `./gradlew :iosApp:syncFramework` to materialize `FinnApp.framework`.
3. Open `iosApp/iosApp.xcodeproj` in Xcode and select the appropriate run destination (simulator or device).

The Swift bridge expects `FinnAppComposeRootViewControllerKt.ComposeRootViewController()` to expose the shared Compose navigation stack. When the shared DI layer evolves, update `MainViewController` in `composeApp/src/iosMain` accordingly; the Swift layer remains thin.
