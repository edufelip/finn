plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.hot.reload) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.spotless)
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**")
        ktlint() // Use default ktlint bundled with Spotless
            .editorConfigOverride(
                mapOf(
                    // Disable max line length (standard:max-line-length)
                    "ktlint_standard_max-line-length" to "disabled",
                    // Disable function naming (standard:function-naming)
                    "ktlint_standard_function-naming" to "disabled",
                    // Disable wildcard imports (standard:no-wildcard-imports)
                    "ktlint_standard_no-wildcard-imports" to "disabled",
                ),
            )
    }
    kotlinGradle {
        target("**/*.kts")
        ktlint().editorConfigOverride(
            mapOf(
                "ktlint_standard_max-line-length" to "disabled",
                "ktlint_standard_function-naming" to "disabled",
                "ktlint_standard_no-wildcard-imports" to "disabled",
            ),
        )
    }
}
