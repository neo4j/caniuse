package org.neo4j.caniuse

import org.assertj.core.api.AssertionsForClassTypes.assertThatCode
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.neo4j.caniuse.CanIUse.canIUse
import org.neo4j.caniuse.Cypher.callInTransactions
import org.neo4j.caniuse.Cypher.callInTransactionsWithCompositeDatabases
import org.neo4j.caniuse.Cypher.callInTransactionsWithCustomErrorPolicy
import org.neo4j.caniuse.Cypher.concurrentCallInTransactions
import org.neo4j.caniuse.Cypher.createDynamicLabels
import org.neo4j.caniuse.Cypher.createDynamicTypes
import org.neo4j.caniuse.Cypher.createIfNotExists
import org.neo4j.caniuse.Cypher.dropIfExists
import org.neo4j.caniuse.Cypher.explicitCypher5Selection
import org.neo4j.caniuse.Cypher.explicitCypherSelection
import org.neo4j.caniuse.Cypher.matchDynamicTypes
import org.neo4j.caniuse.Cypher.mergeDynamicTypes
import org.neo4j.caniuse.Cypher.namedIndexes
import org.neo4j.caniuse.Cypher.removeDynamicLabels
import org.neo4j.caniuse.Cypher.removeDynamicPropertyKeys
import org.neo4j.caniuse.Cypher.setDynamicLabels
import org.neo4j.caniuse.Cypher.setDynamicPropertyKeys
import org.neo4j.caniuse.Cypher.showConstraints
import org.neo4j.caniuse.Cypher.showIndexes
import org.neo4j.caniuse.Dbms.compositeDatabases
import org.neo4j.caniuse.Dbms.multiDatabase
import org.neo4j.caniuse.Schema.propertyListTypeConstraints
import org.neo4j.caniuse.Schema.propertyTypeConstraints
import org.neo4j.caniuse.Schema.propertyUnionTypeConstraints
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.SessionConfig
import org.neo4j.driver.exceptions.ClientException
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class CanIUseIT {
  @Test
  fun supports_call_in_transactions() {
    verify(callInTransactions(), "MATCH (n) CALL { WITH n DETACH DELETE n } IN TRANSACTIONS")
  }

  @Test
  fun supports_call_in_transactions_with_custom_error_policy() {
    verify(
        callInTransactionsWithCustomErrorPolicy(),
        "MATCH (n) CALL { WITH n DETACH DELETE n } IN TRANSACTIONS ON ERROR CONTINUE")
  }

  @Test
  fun supports_call_in_transactions_with_composite_databases() {
    verify(
        callInTransactionsWithCompositeDatabases(),
        ("UNWIND graph.names() AS graphName CALL { " +
            "  USE graph.byName( graphName ) " +
            "  MATCH (n) " +
            "  RETURN elementId(n) AS id " +
            "} " +
            "CALL { " +
            "  USE graph.byName( graphName ) " +
            "  WITH id " +
            "  MATCH (n) " +
            "  WHERE elementId(n) = id " +
            "  DETACH DELETE n " +
            "} IN TRANSACTIONS"),
        SessionConfig.forDatabase("inventory"))
  }

  @Test
  fun supports_concurrent_call_in_transactions() {
    verify(
        concurrentCallInTransactions(),
        "MATCH (n) CALL { WITH n DETACH DELETE n } IN CONCURRENT TRANSACTIONS")
  }

  @Test
  fun supports_named_indexes() {
    verify(namedIndexes(), "CREATE INDEX a_name FOR (n:Node) ON (n.prop)")
  }

  @Test
  fun supports_drop_if_exists() {
    verify(dropIfExists(), "DROP INDEX another_name IF EXISTS")
  }

  @Test
  fun supports_create_if_not_exists() {
    verify(createIfNotExists(), "CREATE INDEX a_name IF NOT EXISTS FOR (n:Node) ON (n.prop)")
  }

  @Test
  fun supports_show_indexes() {
    verify(showIndexes(), "SHOW INDEXES")
  }

  @Test
  fun supports_show_constraints() {
    verify(showConstraints(), "SHOW CONSTRAINTS")
  }

  @Test
  fun supports_set_dynamic_labels() {
    verify(setDynamicLabels(), "MATCH (n) WHERE n.name IS NOT NULL SET n:$(n.name)")
  }

  @Test
  fun supports_remove_dynamic_labels() {
    verify(removeDynamicLabels(), "MATCH (n) WHERE n.name IS NOT NULL REMOVE n:$(n.name)")
  }

  @Test
  fun supports_set_dynamic_property_keys() {
    verify(setDynamicPropertyKeys(), "MATCH (n) SET n[n.name + \"Copy\"] = \"foobar\"")
  }

  @Test
  fun supports_remove_dynamic_property_keys() {
    verify(removeDynamicPropertyKeys(), "MATCH (n) REMOVE n[n.name + \"Copy\"]")
  }

  @Test
  fun supports_create_dynamic_labels() {
    verify(createDynamicLabels(), "WITH \"Label\" AS label CREATE (n:$(label))")
  }

  @Test
  fun supports_match_dynamic_labels() {
    verify(createDynamicLabels(), "WITH \"Label\" AS label MATCH (n:$(label)) RETURN n")
  }

  @Test
  fun supports_merge_dynamic_labels() {
    verify(createDynamicLabels(), "WITH \"Label\" AS label MERGE (n:$(label)) RETURN n")
  }

  @Test
  fun supports_create_dynamic_types() {
    verify(
        createDynamicTypes(), "WITH \"Type\" AS type MATCH (n:Foo) " + "CREATE (n)-[:$(type)]->(n)")
  }

  @Test
  fun supports_match_dynamic_types() {
    verify(
        matchDynamicTypes(),
        "WITH \"Type\" AS type MATCH (n:Foo) " + "MATCH (n)-[r:$(type)]->(n) RETURN r")
  }

  @Test
  fun supports_merge_dynamic_types() {
    verify(
        mergeDynamicTypes(),
        "WITH \"Type\" AS type MATCH (n:Foo) " + "MERGE (n)-[r:$(type)]->(n) RETURN r")
  }

  @Test
  fun supports_multi_databases() {
    // composite databases is implicitly tested in the test setup
    verify(
        multiDatabase(), "CREATE OR REPLACE DATABASE foobar", SessionConfig.forDatabase("system"))
  }

  @Test
  fun supports_property_type_constraint() {
    verify(
        propertyTypeConstraints(), "CREATE CONSTRAINT c1 FOR (c:Foobar) REQUIRE c.baz IS :: STRING")
  }

  @Test
  fun supports_property_list_type_constraint() {
    verify(
        propertyListTypeConstraints(),
        "CREATE CONSTRAINT c2 FOR (c:Foobar) REQUIRE c.qix IS :: LIST<BOOLEAN NOT NULL>")
  }

  @Test
  fun supports_property_union_type_constraint() {
    verify(
        propertyUnionTypeConstraints(),
        "CREATE CONSTRAINT c3 FOR (c:Foobar) REQUIRE c.mux IS :: INTEGER | FLOAT | STRING")
  }

  @Test
  fun supports_cypher_explicit_version_selection() {
    verify(explicitCypherSelection(), "CYPHER 5 RETURN 42")
  }

  @Test
  fun supports_cypher_explicit_version_5_selection() {
    verify(explicitCypher5Selection(), "CYPHER 5 RETURN 42")
  }

  private fun verify(
      check: Neo4jPredicate,
      query: String,
      config: SessionConfig = SessionConfig.forDatabase("neo4j")
  ) {
    if (canIUse(check).withNeo4j(Neo4j.detectedWith(driver))) {
      assertThatCode { runQuery(query, config) }.doesNotThrowAnyException()
    } else {
      assertThatThrownBy { runQuery(query, config) }.isInstanceOf(ClientException::class.java)
    }
  }

  private fun runQuery(query: String, config: SessionConfig) {
    driver.session(config).use { session -> session.run(query).consume() }
  }

  companion object {
    @Container
    private val neo4j: Neo4jContainer<*> =
        Neo4jContainer(DockerNeo4j.image())
            .withEnv("NEO4J_ACCEPT_LICENSE_AGREEMENT", "yes")
            .withAdminPassword("letmein!")

    private lateinit var driver: Driver

    @BeforeAll
    @JvmStatic
    fun beforeAll() {
      driver = GraphDatabase.driver(neo4j.boltUrl, AuthTokens.basic("neo4j", "letmein!"))
      driver.verifyConnectivity()
      if (canIUse(compositeDatabases()).withNeo4j(Neo4j.detectedWith(driver))) {
        driver.session(SessionConfig.forDatabase("system")).use { session ->
          session.run("CREATE OR REPLACE COMPOSITE DATABASE inventory")
        }
      }
    }

    @AfterAll
    @JvmStatic
    fun afterAll() {
      driver.close()
    }
  }
}
