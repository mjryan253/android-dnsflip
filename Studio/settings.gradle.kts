pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://api.xposed.info/") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.rikka.dev/releases") }
        maven { url = uri("https://api.rikka.dev/releases") }
        maven { url = uri("https://maven.rikka.dev/snapshots") }
    }
}

rootProject.name = "DNSFlip"
include(":dnsflip")
