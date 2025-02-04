package com.neo4j.data.importer

import com.neo4j.data.importer.CanIUse.canIUse
import com.neo4j.data.importer.Cypher.callInTransactions
import com.neo4j.data.importer.Cypher.callInTransactionsWithCompositeDatabases
import com.neo4j.data.importer.Cypher.callInTransactionsWithCustomErrorPolicy
import com.neo4j.data.importer.Cypher.concurrentCallInTransactions
import com.neo4j.data.importer.Cypher.createDynamicLabels
import com.neo4j.data.importer.Cypher.createDynamicTypes
import com.neo4j.data.importer.Cypher.createIfNotExists
import com.neo4j.data.importer.Cypher.dropIfExists
import com.neo4j.data.importer.Cypher.matchDynamicTypes
import com.neo4j.data.importer.Cypher.mergeDynamicTypes
import com.neo4j.data.importer.Cypher.namedIndexes
import com.neo4j.data.importer.Cypher.removeDynamicLabels
import com.neo4j.data.importer.Cypher.removeDynamicPropertyKeys
import com.neo4j.data.importer.Cypher.setDynamicLabels
import com.neo4j.data.importer.Cypher.setDynamicPropertyKeys
import com.neo4j.data.importer.Cypher.showConstraints
import com.neo4j.data.importer.Cypher.showIndexes
import com.neo4j.data.importer.Dbms.compositeDatabases
import com.neo4j.data.importer.Dbms.multiDatabase
import com.neo4j.data.importer.Schema.propertyListTypeConstraints
import com.neo4j.data.importer.Schema.propertyTypeConstraints
import com.neo4j.data.importer.Schema.propertyUnionTypeConstraints
import org.assertj.core.api.AssertionsForClassTypes.assertThatCode
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.SessionConfig
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

  private fun verify(
      check: Neo4jPredicate,
      query: String,
      config: SessionConfig = SessionConfig.forDatabase("neo4j")
  ) {
    assertThatCode {
          if (canIUse(check).withNeo4j(Neo4j.detectedWith(driver))) {
            driver.session(config).use { session -> session.run(query).consume() }
          }
        }
        .doesNotThrowAnyException()
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
