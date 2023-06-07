plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.13.3"
}

val pluginVersion = "2.0.4"

group = "com.thelumiereguy.compose_helper"
version = pluginVersion

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2023.1.1")
    plugins.set(listOf("com.intellij.java", "Kotlin"))
}

kotlin {
    jvmToolchain {
        version = "1.8"
    }
}
tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<JavaCompile> {
        targetCompatibility = "11"
        sourceCompatibility = "11"
    }

    publishPlugin {
        token.set(System.getenv("Jetbrains_Compose_Helper_Token"))
    }


    patchPluginXml {
        version.set(pluginVersion)
        sinceBuild.set("211.*")
        untilBuild.set("231.*")
        changeNotes.set(
            """
           <ul>
             <li><b>2.0.4</b> Added support for Android Studio - Giraffe and Hedgehog</li>
             <li><b>2.0.3</b> Added support for Android Studio - Electric Eel and Flamingo</li>
             <li><b>2.0.2</b> Added support for Android Studio - Chipmunk and Dolphin</li>
             <li><b>2.0.1</b> Bug fix - Parent Composable not actually being removed in some cases</li>
             <li><b>2.0.0</b> Complete Rewrite - Optimizations and added new features</li>
             <li><b>1.0.2</b> Added support for older Android Studio versions</li>
             <li><b>1.0.1</b> Added support for Artic Fox</li>
             <li><b>1.0.0</b> Initial Version</li>
          </ul>    
            """.trimIndent()
        )
    }
}
