import org.gradle.api.tasks.Sync
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.util.Locale

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.hot.reload)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(JavaVersion.VERSION_17.toString()))
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FinnApp"
            isStatic = true
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.coil.compose)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.compose.tooling.preview)
        }
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(projects.shared)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "com.edufelip.finn.composeapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        disable += "NullSafeMutableLiveData"
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.edufelip.finn.composeapp.DesktopAppKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "FinnDesktop"
            packageVersion = "1.0.0"
        }
    }
}

val kotlinExt = extensions.getByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>()

fun resolveIosTargetName(sdkName: String): String = when {
    sdkName.startsWith("iphoneos", ignoreCase = true) -> "iosArm64"
    sdkName.startsWith("iphonesimulator", ignoreCase = true) -> "iosSimulatorArm64"
    else -> error("Unsupported SDK_NAME=$sdkName. Expected iphoneos* or iphonesimulator*.")
}

val packFinnAppForXcode = tasks.register<Sync>("packFinnAppForXcode") {
    group = "compose ios"
    description = "Packages the FinnApp framework for consumption by Xcode."

    val rawConfiguration = System.getenv("CONFIGURATION") ?: "Debug"
    val mode = rawConfiguration.uppercase(Locale.US)
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = resolveIosTargetName(sdkName)

    val target = kotlinExt.targets.getByName(targetName) as KotlinNativeTarget
    val framework = target.binaries.getFramework(mode)

    dependsOn(framework.linkTaskProvider)

    val outputDir = layout.buildDirectory.dir("xcode-frameworks/$rawConfiguration/$sdkName")
    from(framework.outputDirectory)
    into(outputDir)
}

tasks.register("syncFinnAppFrameworkForXcode") {
    group = "compose ios"
    description = "Alias for packFinnAppForXcode to keep naming parity with iosApp tasks."
    dependsOn(packFinnAppForXcode)
}
