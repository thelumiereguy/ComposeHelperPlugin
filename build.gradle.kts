import org.jetbrains.intellij.ideaDir

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.intellij") version "1.3.0"
}

group = "com.thelumiereguy.compose_helper"
version = "1.0.1"


repositories {
    mavenCentral()
}


dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2020.3.1")
//    version.set("211.7628.21")
    plugins.set(listOf("com.intellij.java", "Kotlin"))
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    publishPlugin {
        token.set(System.getenv("Jetbrains_Compose_Helper_Token"))
    }

    runIde {
        ideDir.set(file("D:\\android-studio"))
    }


    patchPluginXml {
        sinceBuild.set("203.*")
        untilBuild.set("211.*")
        changeNotes.set(
            """
           <ul>
             <li><b>1.0.1</b> Added support for Artic Fox</li>
             <li><b>1.0.0</b> Initial Version</li>
          </ul>
             
             """.trimIndent()
        )
    }
}
