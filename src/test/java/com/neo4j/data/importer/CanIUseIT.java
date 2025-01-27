package com.neo4j.data.importer;

import static com.neo4j.data.importer.CanIUse.canIUse;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class CanIUseIT {

    @Container
    private static final Neo4jContainer<?> neo4j = new Neo4jContainer<>(DockerNeo4j.image())
            .withEnv("NEO4J_ACCEPT_LICENSE_AGREEMENT", "yes")
            .withAdminPassword("letmein!");

    private static Driver driver;

    @BeforeAll
    static void beforeAll() {
        driver = GraphDatabase.driver(neo4j.getBoltUrl(), AuthTokens.basic("neo4j", "letmein!"));
        driver.verifyConnectivity();
        if (canIUse(Dbms.compositeDatabases()).withDetectedNeo4j(driver)) {
            try (Session session = driver.session(SessionConfig.forDatabase("system"))) {
                session.run("CREATE OR REPLACE COMPOSITE DATABASE inventory");
            }
        }
    }

    @AfterAll
    static void afterAll() {
        driver.close();
    }

    @Test
    void supports_call_in_transactions() {
        verify(Cypher.callInTransactions(), "MATCH (n) CALL { WITH n DETACH DELETE n } IN TRANSACTIONS");
    }

    @Test
    void supports_call_in_transactions_with_custom_error_policy() {
        verify(
                Cypher.callInTransactionsWithCustomErrorPolicy(),
                "MATCH (n) CALL { WITH n DETACH DELETE n } IN TRANSACTIONS ON ERROR CONTINUE");
    }

    @Test
    void supports_call_in_transactions_with_composite_databases() {
        verify(
                Cypher.callInTransactionsWithCompositeDatabases(),
                "UNWIND graph.names() AS graphName CALL { "
                        + "  USE graph.byName( graphName ) "
                        + "  MATCH (n) "
                        + "  RETURN elementId(n) AS id "
                        + "} "
                        + "CALL { "
                        + "  USE graph.byName( graphName ) "
                        + "  WITH id "
                        + "  MATCH (n) "
                        + "  WHERE elementId(n) = id "
                        + "  DETACH DELETE n "
                        + "} IN TRANSACTIONS",
                SessionConfig.forDatabase("inventory"));
    }

    @Test
    void supports_concurrent_call_in_transactions() {
        verify(
                Cypher.concurrentCallInTransactions(),
                "MATCH (n) CALL { WITH n DETACH DELETE n } IN CONCURRENT TRANSACTIONS");
    }

    @Test
    void supports_named_indexes() {
        verify(Cypher.namedIndexes(), "CREATE INDEX a_name FOR (n:Node) ON (n.prop)");
    }

    @Test
    void supports_drop_if_exists() {
        verify(Cypher.dropIfExists(), "DROP INDEX another_name IF EXISTS");
    }

    @Test
    void supports_create_if_not_exists() {
        verify(Cypher.createIfNotExists(), "CREATE INDEX a_name IF NOT EXISTS FOR (n:Node) ON (n.prop)");
    }

    @Test
    void supports_show_indexes() {
        verify(Cypher.showIndexes(), "SHOW INDEXES");
    }

    @Test
    void supports_show_constraints() {
        verify(Cypher.showConstraints(), "SHOW CONSTRAINTS");
    }

    @Test
    void supports_set_dynamic_labels() {
        verify(Cypher.setDynamicLabels(), "MATCH (n) WHERE n.name IS NOT NULL SET n:$(n.name)");
    }

    @Test
    void supports_remove_dynamic_labels() {
        verify(Cypher.removeDynamicLabels(), "MATCH (n) WHERE n.name IS NOT NULL REMOVE n:$(n.name)");
    }

    @Test
    void supports_set_dynamic_property_keys() {
        verify(Cypher.setDynamicPropertyKeys(), "MATCH (n) SET n[n.name + \"Copy\"] = \"foobar\"");
    }

    @Test
    void supports_remove_dynamic_property_keys() {
        verify(Cypher.removeDynamicPropertyKeys(), "MATCH (n) REMOVE n[n.name + \"Copy\"]");
    }

    @Test
    void supports_create_dynamic_labels() {
        verify(Cypher.createDynamicLabels(), "WITH \"Label\" AS label CREATE (n:$(label))");
    }

    @Test
    void supports_match_dynamic_labels() {
        verify(Cypher.createDynamicLabels(), "WITH \"Label\" AS label MATCH (n:$(label)) RETURN n");
    }

    @Test
    void supports_merge_dynamic_labels() {
        verify(Cypher.createDynamicLabels(), "WITH \"Label\" AS label MERGE (n:$(label)) RETURN n");
    }

    @Test
    void supports_create_dynamic_types() {
        verify(Cypher.createDynamicTypes(), "WITH \"Type\" AS type MATCH (n:Foo) " + "CREATE (n)-[:$(type)]->(n)");
    }

    @Test
    void supports_match_dynamic_types() {
        verify(
                Cypher.matchDynamicTypes(),
                "WITH \"Type\" AS type MATCH (n:Foo) " + "MATCH (n)-[r:$(type)]->(n) RETURN r");
    }

    @Test
    void supports_merge_dynamic_types() {
        verify(
                Cypher.mergeDynamicTypes(),
                "WITH \"Type\" AS type MATCH (n:Foo) " + "MERGE (n)-[r:$(type)]->(n) RETURN r");
    }

    @Test
    void supports_multi_databases() {
        // composite databases is implicitly tested in the test setup
        verify(Dbms.multiDatabase(), "CREATE OR REPLACE DATABASE foobar", SessionConfig.forDatabase("system"));
    }

    @Test
    void supports_property_type_constraint() {
        verify(Schema.propertyTypeConstraints(), "CREATE CONSTRAINT c1 FOR (c:Foobar) REQUIRE c.baz IS :: STRING");
    }

    @Test
    void supports_property_list_type_constraint() {
        verify(
                Schema.propertyListTypeConstraints(),
                "CREATE CONSTRAINT c2 FOR (c:Foobar) REQUIRE c.qix IS :: LIST<BOOLEAN NOT NULL>");
    }

    @Test
    void supports_property_union_type_constraint() {
        verify(
                Schema.propertyUnionTypeConstraints(),
                "CREATE CONSTRAINT c3 FOR (c:Foobar) REQUIRE c.mux IS :: INTEGER | FLOAT | STRING");
    }

    private void verify(Neo4jPredicate check, String query) {
        verify(check, query, SessionConfig.forDatabase("neo4j"));
    }

    private void verify(Neo4jPredicate check, String query, SessionConfig config) {
        assertThatCode(() -> {
                    if (canIUse(check).withDetectedNeo4j(driver)) {
                        try (Session session = driver.session(config)) {
                            session.run(query).consume();
                        }
                    }
                })
                .doesNotThrowAnyException();
    }
}
