plugins {
//    id("java")
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.intellij") version "1.3.0"
}

group = "com.thelumiereguy.composeplugin"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://maven.google.com/") }
    maven { url = uri("https://dl.bintray.com/kotlin/uast/") }
}


dependencies {
//    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.5.10")

}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2020.3.1")
    plugins.set(listOf("com.intellij.java", "Kotlin"))
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
//    runIde {
//        ideDir.set(file("D:\\android-studio"))
//    }

    patchPluginXml {
        changeNotes.set(
            """
            Add change notes here.<br>
            <em>most HTML tags may be used</em>        """.trimIndent()
        )
    }
}
