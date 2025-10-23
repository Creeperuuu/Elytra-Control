plugins {
    id("fabric-conventions")
}

val fabricVersion: String by project
val modmenuVersion: String by project
val smoothiezApiVersion: String by project

repositories {
    mavenLocal()
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
}

dependencies {
    modApi("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")
    modApi("io.github.smootheez:smoothiezapi:${smoothiezApiVersion}")

    modCompileOnly("com.terraformersmc:modmenu:${modmenuVersion}")
    modLocalRuntime("com.terraformersmc:modmenu:${modmenuVersion}")
}