plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    implementation("net.fabricmc:fabric-loom:1.14.8")
}

gradlePlugin {
    plugins {
        create("fabricConventions") {
            id = "fabric-conventions"
            implementationClass = "FabricConventionsPlugin"
        }
    }
}
