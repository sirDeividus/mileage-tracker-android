// settings.gradle.kts
// -----------------------------------------------------------------------
// Este archivo le dice a Gradle (el sistema de compilación de Android)
// qué módulos forman parte del proyecto. Un proyecto Android puede tener
// varios módulos (por ejemplo: app, librería, wear-app), pero el nuestro
// solo tiene uno: "app".
// -----------------------------------------------------------------------

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Mileage Tracker"
include(":app")
