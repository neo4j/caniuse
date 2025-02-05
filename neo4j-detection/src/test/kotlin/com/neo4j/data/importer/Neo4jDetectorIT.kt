package com.neo4j.data.importer

import com.neo4j.data.importer.DockerNeo4j.enterprise
import com.neo4j.data.importer.DockerNeo4j.image
import com.neo4j.data.importer.DockerNeo4j.version
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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
      val neo4j = Neo4j.detectedWith(driver)
      assertThat(neo4j.edition)
          .isEqualTo(if (enterprise()) Neo4jEdition.ENTERPRISE else Neo4jEdition.COMMUNITY)
      // we cannot match more than the major since "5" is a valid tag
      assertThat(neo4j.version.major).isEqualTo(majorOf(version()))
      assertThat(neo4j.deploymentType).isGreaterThanOrEqualTo(Neo4jDeploymentType.SELF_MANAGED)
    }
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
        Neo4jContainer(image())
            .withEnv("NEO4J_ACCEPT_LICENSE_AGREEMENT", "yes")
            .withAdminPassword("letmein!")
  }
}
