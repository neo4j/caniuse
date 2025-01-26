package com.neo4j.data.importer;

import static com.neo4j.data.importer.Neo4jVersion.V5_0_0;
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
    private static final Neo4jContainer<?> enterprise = new Neo4jContainer<>("neo4j:5-enterprise")
            .withEnv("NEO4J_ACCEPT_LICENSE_AGREEMENT", "yes")
            .withAdminPassword("letmein!");

    @Container
    private static final Neo4jContainer<?> community = new Neo4jContainer<>("neo4j:5")
            .withEnv("NEO4J_ACCEPT_LICENSE_AGREEMENT", "yes")
            .withAdminPassword("letmein!");

    @Test
    void detects_neo4j_EE_instance() {
        try (Driver driver = GraphDatabase.driver(enterprise.getBoltUrl(), AuthTokens.basic("neo4j", "letmein!"))) {
            Neo4j neo4j = Neo4jDetector.detect(driver);

            assertThat(neo4j.edition()).isEqualTo(Neo4jEdition.ENTERPRISE);
            assertThat(neo4j.version()).isGreaterThanOrEqualTo(V5_0_0);
            assertThat(neo4j.environment()).isGreaterThanOrEqualTo(Neo4jEnvironment.ON_PREMISE);
        }
    }

    @Test
    void detects_neo4j_CE_instance() {
        try (Driver driver = GraphDatabase.driver(community.getBoltUrl(), AuthTokens.basic("neo4j", "letmein!"))) {
            Neo4j neo4j = Neo4jDetector.detect(driver);

            assertThat(neo4j.edition()).isEqualTo(Neo4jEdition.COMMUNITY);
            assertThat(neo4j.version()).isGreaterThanOrEqualTo(V5_0_0);
            assertThat(neo4j.environment()).isGreaterThanOrEqualTo(Neo4jEnvironment.ON_PREMISE);
        }
    }
}
