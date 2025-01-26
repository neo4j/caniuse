package com.neo4j.data.importer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class Neo4jDetectorIT {

    @Container
    private static final Neo4jContainer<?> neo4j = new Neo4jContainer<>(DockerNeo4j.image())
            .withEnv("NEO4J_ACCEPT_LICENSE_AGREEMENT", "yes")
            .withAdminPassword("letmein!");

    @Test
    void detects_neo4j_instance() {
        try (Driver driver = GraphDatabase.driver(neo4j.getBoltUrl(), AuthTokens.basic("neo4j", "letmein!"))) {
            Neo4j neo4j = Neo4jDetector.detect(driver);

            assertThat(neo4j.edition())
                    .isEqualTo(DockerNeo4j.enterprise() ? Neo4jEdition.ENTERPRISE : Neo4jEdition.COMMUNITY);
            // we cannot match more than the major since "5" is a valid tag
            assertThat(neo4j.version().major()).isEqualTo(majorOf(DockerNeo4j.version()));
            assertThat(neo4j.environment()).isGreaterThanOrEqualTo(Neo4jEnvironment.ON_PREMISE);
        }
    }

    private int majorOf(String version) {
        int dot = version.indexOf('.');
        if (dot == -1) {
            return Integer.parseInt(version, 10);
        }
        String major = version.substring(0, dot);
        return Integer.parseInt(major, 10);
    }
}
