import org.jetbrains.kotlin.gradle.plugin.statistics.ReportStatisticsToElasticSearch.url

plugins {
    id("java")
    kotlin("jvm") version "1.6.0"
    id("org.jetbrains.intellij") version "1.3.0"
}

group = "com.thelumiereguy.composeplugin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.6.0")
//    implementation("androidx.compose.runtime:runtime:1.0.5")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2021.1.1")
    plugins.set(listOf("java", "Kotlin"))
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
