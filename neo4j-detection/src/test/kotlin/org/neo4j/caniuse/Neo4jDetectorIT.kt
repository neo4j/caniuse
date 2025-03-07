package org.neo4j.caniuse

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.neo4j.caniuse.DockerNeo4j.enterprise
import org.neo4j.caniuse.DockerNeo4j.image
import org.neo4j.caniuse.DockerNeo4j.version
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
internal class Neo4jDetectorIT {
    @Test
    fun detects_neo4j_instance() {
        GraphDatabase.driver(neo4j.boltUrl, AuthTokens.basic("neo4j", "letmein!")).use { driver ->
            val neo4j = Neo4jDetector.detect(driver)
            assertThat(neo4j.edition).isEqualTo(if (enterprise()) Neo4jEdition.ENTERPRISE else Neo4jEdition.COMMUNITY)
            val version = version()
            // ignore calver versions for now, since they expose a "fake" semver version
            if (!followsCalver(version)) {
                // we cannot match more than the major since "5" is a valid tag
                assertThat(neo4j.version.major).isEqualTo(majorOf(version))
            }
            assertThat(neo4j.deploymentType).isEqualTo(Neo4jDeploymentType.SELF_MANAGED)
        }
    }

    private fun followsCalver(version: String): Boolean {
        val dot = version.indexOf('.')
        if (dot == -1) {
            return version.length == 4
        }
        return version.substring(0, dot).length == 4
    }

    private fun majorOf(version: String): Int {
        val dot = version.indexOf('.')
        if (dot == -1) {
            return version.toInt(10)
        }
        val major = version.substring(0, dot)
        return major.toInt(10)
    }

    companion object {
        @Container
        private val neo4j: Neo4jContainer<*> =
            Neo4jContainer(image()).withEnv("NEO4J_ACCEPT_LICENSE_AGREEMENT", "yes").withAdminPassword("letmein!")
    }
}
