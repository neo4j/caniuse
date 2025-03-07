package org.neo4j.caniuse

object DockerNeo4j {
    fun image(): String {
        return String.format("neo4j:%s", tag())
    }

    private fun tag(): String {
        val version = version()
        if (enterprise()) {
            return String.format("%s-enterprise", version)
        }
        return version
    }

    fun version(): String {
        val version = System.getenv("NEO4J_VERSION") ?: return "4.4"
        return version
    }

    fun enterprise(): Boolean {
        return System.getenv("ENTERPRISE").toBoolean()
    }
}
