import org.gradle.api.tasks.Exec

plugins {
    base
}

val embedAndSignTask = tasks.register("embedFinnAppFrameworkForXcode") {
    group = "compose ios"
    description = "Builds and copies the FinnApp framework for consumption by Xcode."
    dependsOn(":composeApp:syncFinnAppFrameworkForXcode")
}

tasks.register("syncFramework") {
    group = "compose ios"
    description = "Convenience alias for embedFinnAppFrameworkForXcode."
    dependsOn(embedAndSignTask)
}

// Allow `./gradlew :iosApp:openXcode` to configure/remove build artifacts before launching Xcode.
tasks.register<Exec>("openXcode") {
    group = "compose ios"
    description = "Opens the iOS Xcode workspace after ensuring FinnApp framework is ready."
    dependsOn(embedAndSignTask)
    commandLine("open", project.layout.projectDirectory.dir("iosApp.xcodeproj").asFile.absolutePath)
    isIgnoreExitValue = true
    onlyIf {
        System.getProperty("os.name")?.contains("mac", ignoreCase = true) == true
    }
}
