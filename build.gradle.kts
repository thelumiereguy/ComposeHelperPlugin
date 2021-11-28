plugins {
    id("org.jetbrains.intellij") version "1.3.0"
    id("org.jetbrains.kotlin.jvm") version "1.6.0"
}

group = "com.thelumiereguy.composeplugin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2021.1.1")
    plugins.set(listOf("java", "Kotlin"))
    type.set("IC")
}


tasks {
    runIde {
        ideDir.set(file("D:\\android-studio"))
    }


    patchPluginXml {
        changeNotes.set(
            """
            Add change notes here.<br>
            <em>most HTML tags may be used</em>        """.trimIndent()
        )
    }
}
