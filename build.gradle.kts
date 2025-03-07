plugins {
    id("com.adarshr.test-logger") version "4.0.0" apply false
}

subprojects {
    plugins.apply("buildlogic.kotlin-conventions")
    plugins.apply("com.adarshr.test-logger")

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
