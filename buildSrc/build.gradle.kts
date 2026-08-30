plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    // Keep buildSrc on a Loom release compiled with the Kotlin version
    // bundled with Gradle 8.14. The main project can use its configured
    // Loom version independently.
    implementation("net.fabricmc:fabric-loom:1.11.8")
}

gradlePlugin {
    plugins {
        create("fabricConventions") {
            id = "fabric-conventions"
            implementationClass = "FabricConventionsPlugin"
        }
    }
}
