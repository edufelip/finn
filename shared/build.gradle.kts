plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.detekt)
}

kotlin {
    // Align Kotlin JVM target with Java 17 to avoid mismatch
    jvmToolchain(17)
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.resources)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.koin.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.retrofit)
                // Skia Image APIs for iOS byte decoding are provided by Compose runtime on iOS
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.coil.compose)
                implementation(libs.androidx.compose.tooling.preview)
                implementation(libs.sqldelight.android.driver)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.sqldelight.sqlite.driver)
            }
        }
        val iosX64Main by getting { dependencies { implementation(libs.kamel.image); implementation(libs.sqldelight.native.driver) } }
        val iosArm64Main by getting { dependencies { implementation(libs.kamel.image); implementation(libs.sqldelight.native.driver) } }
        val iosSimulatorArm64Main by getting { dependencies { implementation(libs.kamel.image); implementation(libs.sqldelight.native.driver) } }

        all {
            languageSettings.languageVersion = "2.0"
            languageSettings.apiVersion = "2.0"
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll("-Xexpect-actual-classes")
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.framework {
            baseName = "SharedUI"
            isStatic = false
        }
    }
}

sqldelight {
    databases {
        create("FinnDatabase") {
            packageName.set("com.edufelip.finn.shared.cache")
            schemaOutputDirectory.set(layout.projectDirectory.dir("src/commonMain/sqldelight/databases"))
        }
    }
}

android {
    namespace = "com.edufelip.finn.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
}

// Compose Previews from shared Android source need ui-tooling at debug time
dependencies {
    debugImplementation(libs.androidx.compose.ui.tooling)
}
