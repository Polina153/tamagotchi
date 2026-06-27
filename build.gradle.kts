plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "tamagotchi"
version = "1.0"

repositories {
    mavenCentral()
}

// Нет внешних зависимостей — только Kotlin stdlib

application {
    mainClass.set("tamagotchi.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "tamagotchi.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
