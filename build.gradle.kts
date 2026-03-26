// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.spotless)
}

spotless {
    kotlin {
        target("app/src/**/*.kt")
        ktlint()
    }
    kotlinGradle {
        target("build.gradle.kts", "settings.gradle.kts", "app/build.gradle.kts")
        ktlint()
    }
}

tasks.register("ktlintCheck") {
    dependsOn(tasks.named("spotlessCheck"))
}

tasks.register("testDebug") {
    dependsOn(":app:testDebugUnitTest")
}
