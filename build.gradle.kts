import com.diffplug.gradle.spotless.SpotlessCheck
import com.diffplug.gradle.spotless.SpotlessExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.adarshr.test-logger") version "4.0.0" apply false
    id("com.diffplug.spotless") version "7.0.2" apply false
}

subprojects {
    plugins.apply("buildlogic.kotlin-conventions")
    plugins.apply("com.diffplug.spotless")
    plugins.apply("com.adarshr.test-logger")

    configure<SpotlessExtension> {
        kotlin {
            ktfmt("0.46")
                .kotlinlangStyle()
                .configure {
                    it.setBlockIndent(4)
                    it.setContinuationIndent(4)
                    it.setRemoveUnusedImports(true)
                    it.setMaxWidth(120)
                }
            toggleOffOn()
        }
    }

    tasks.named<KotlinCompile>("compileKotlin") {
        dependsOn("spotlessCheck")
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
        exclude("**/*IT.class")
    }

    tasks.register<Test>("integrationTest") {
        description = "Runs integration tests."
        group = "verification"
        useJUnitPlatform()
        include("**/*IT.class")
        shouldRunAfter("test")
    }
}
