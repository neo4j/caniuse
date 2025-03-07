rootProject.name = "caniuse-parent"
include(":caniuse-neo4j-detection")
include(":caniuse-core")
include(":caniuse-api")
project(":caniuse-neo4j-detection").projectDir = file("neo4j-detection")
project(":caniuse-core").projectDir = file("core")
project(":caniuse-api").projectDir = file("api")
