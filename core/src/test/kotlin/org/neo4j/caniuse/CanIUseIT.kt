/*
 * Copyright (c) "Neo4j"
 * Neo4j Sweden AB [https://neo4j.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.caniuse

import kotlin.reflect.KFunction
import org.assertj.core.api.AssertionsForClassTypes.assertThatCode
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.neo4j.caniuse.CanIUse.canIUse
import org.neo4j.caniuse.Dbms.compositeDatabases
import org.neo4j.caniuse.Schema.nodePropertyUniquenessConstraints
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.SessionConfig
import org.neo4j.driver.exceptions.Neo4jException
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class CanIUseIT {
  @Test
  fun supports_call_in_transactions() {
    verify(Cypher::callInTransactions, "MATCH (n) CALL { WITH n DETACH DELETE n } IN TRANSACTIONS")
  }

  @Test
  fun supports_call_in_transactions_with_custom_error_policy() {
    verify(
        Cypher::callInTransactionsWithCustomErrorPolicy,
        "MATCH (n) CALL { WITH n DETACH DELETE n } IN TRANSACTIONS ON ERROR CONTINUE")
  }

  @Test
  fun supports_call_in_transactions_with_composite_databases() {
    verify(
        Cypher::callInTransactionsWithCompositeDatabases,
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
        Cypher::concurrentCallInTransactions,
        "MATCH (n) CALL { WITH n DETACH DELETE n } IN CONCURRENT TRANSACTIONS")
  }

  @Test
  fun supports_named_indexes() {
    verify(Cypher::namedIndexes, "CREATE INDEX a_name FOR (n:Node) ON (n.prop)")
  }

  @Test
  fun supports_drop_if_exists() {
    verify(Cypher::dropIfExists, "DROP INDEX another_name IF EXISTS")
  }

  @Test
  fun supports_create_if_not_exists() {
    verify(Cypher::createIfNotExists, "CREATE INDEX a_name IF NOT EXISTS FOR (n:Node) ON (n.prop)")
  }

  @Test
  fun supports_show_indexes() {
    verify(Cypher::showIndexes, "SHOW INDEXES")
  }

  @Test
  fun supports_show_constraints() {
    verify(Cypher::showConstraints, "SHOW CONSTRAINTS")
  }

  @Test
  fun supports_set_dynamic_labels() {
    verify(Cypher::setDynamicLabels, "MATCH (n) WHERE n.name IS NOT NULL SET n:$(n.name)")
  }

  @Test
  fun supports_remove_dynamic_labels() {
    verify(Cypher::removeDynamicLabels, "MATCH (n) WHERE n.name IS NOT NULL REMOVE n:$(n.name)")
  }

  @Test
  fun supports_set_dynamic_property_keys() {
    verify(Cypher::setDynamicPropertyKeys, "MATCH (n) SET n[n.name + \"Copy\"] = \"foobar\"")
  }

  @Test
  fun supports_remove_dynamic_property_keys() {
    verify(Cypher::removeDynamicPropertyKeys, "MATCH (n) REMOVE n[n.name + \"Copy\"]")
  }

  @Test
  fun supports_create_dynamic_labels() {
    verify(Cypher::createDynamicLabels, "WITH \"Label\" AS label CREATE (n:$(label))")
  }

  @Test
  fun supports_match_dynamic_labels() {
    verify(Cypher::createDynamicLabels, "WITH \"Label\" AS label MATCH (n:$(label)) RETURN n")
  }

  @Test
  fun supports_merge_dynamic_labels() {
    verify(Cypher::createDynamicLabels, "WITH \"Label\" AS label MERGE (n:$(label)) RETURN n")
  }

  @Test
  fun supports_create_dynamic_types() {
    verify(
        Cypher::createDynamicTypes,
        "WITH \"Type\" AS type MATCH (n:Foo) " + "CREATE (n)-[:$(type)]->(n)")
  }

  @Test
  fun supports_match_dynamic_types() {
    verify(
        Cypher::matchDynamicTypes,
        "WITH \"Type\" AS type MATCH (n:Foo) " + "MATCH (n)-[r:$(type)]->(n) RETURN r")
  }

  @Test
  fun supports_merge_dynamic_types() {
    verify(
        Cypher::mergeDynamicTypes,
        "WITH \"Type\" AS type MATCH (n:Foo) " + "MERGE (n)-[r:$(type)]->(n) RETURN r")
  }

  @Test
  fun supports_multi_databases() {
    // composite databases is implicitly tested in the test setup
    verify(
        Dbms::multiDatabase,
        "CREATE OR REPLACE DATABASE foobar",
        SessionConfig.forDatabase("system"))
  }

  @Test
  fun supports_property_type_constraint() {
    verify(
        Schema::propertyTypeConstraints,
        "CREATE CONSTRAINT c1 FOR (c:Foobar) REQUIRE c.baz IS :: STRING")
  }

  @Test
  fun supports_property_list_type_constraint() {
    verify(
        Schema::propertyListTypeConstraints,
        "CREATE CONSTRAINT c2 FOR (c:Foobar) REQUIRE c.qix IS :: LIST<BOOLEAN NOT NULL>")
  }

  @Test
  fun supports_property_union_type_constraint() {
    verify(
        Schema::propertyUnionTypeConstraints,
        "CREATE CONSTRAINT c3 FOR (c:Foobar) REQUIRE c.mux IS :: INTEGER | FLOAT | STRING")
  }

  @Test
  fun supports_cypher_explicit_version_selection() {
    verify(Cypher::explicitCypherSelection, "CYPHER 5 RETURN 42")
  }

  @Test
  fun supports_cypher_explicit_version_5_selection() {
    verify(Cypher::explicitCypher5Selection, "CYPHER 5 RETURN 42")
  }

  @Test
  fun supports_change_data_capture() {
    verify(
        Dbms::changeDataCapture,
        "CREATE OR REPLACE DATABASE foobar OPTIONS {txLogEnrichment: 'FULL'}",
        SessionConfig.forDatabase("system"))
  }

  @Test
  fun supports_node_property_uniqueness_constraint() {
    verify(
        Schema::nodePropertyUniquenessConstraints,
        { neo4j ->
          val name = constraintNameOrEmpty(neo4j, "c4")
          if (canIUse(Cypher.constraintsWithRequireKeyword()).withNeo4j(neo4j)) {
            "CREATE CONSTRAINT $name FOR (c:Foobar1) REQUIRE c.x IS UNIQUE"
          } else {
            "CREATE CONSTRAINT $name ON (c:Foobar1) ASSERT c.x IS UNIQUE"
          }
        })
  }

  @Test
  fun supports_node_property_existence_constraint() {
    verify(
        Schema::nodePropertyExistenceConstraints,
        { neo4j ->
          val name = constraintNameOrEmpty(neo4j, "c5")
          if (canIUse(Cypher.constraintsWithRequireKeyword()).withNeo4j(neo4j)) {
            "CREATE CONSTRAINT $name FOR (c:Foobar2) REQUIRE c.x IS NOT NULL"
          } else {
            "CREATE CONSTRAINT $name FOR (c:Foobar2) ASSERT exists(c.x)"
          }
        })
  }

  @Test
  fun supports_node_key_constraint() {
    verify(
        Schema::nodeKeyConstraints, "CREATE CONSTRAINT c6 FOR (c:Foobar3) REQUIRE c.x IS NODE KEY")
  }

  @Test
  fun supports_relationship_property_uniqueness_constraint() {
    verify(
        Schema::relationshipPropertyUniquenessConstraints,
        "CREATE CONSTRAINT c7 FOR ()-[r:Related1]->() REQUIRE r.x IS UNIQUE")
  }

  @Test
  fun supports_relationship_property_existence_constraint() {
    verify(
        Schema::relationshipPropertyExistenceConstraints,
        { neo4j ->
          val name = constraintNameOrEmpty(neo4j, "c8")
          if (canIUse(Cypher.constraintsWithRequireKeyword()).withNeo4j(neo4j)) {
            "CREATE CONSTRAINT $name FOR ()-[r:Related2]->() REQUIRE r.x IS NOT NULL"
          } else {
            "CREATE CONSTRAINT $name ON ()-[r:Related2]->() ASSERT exists(r.x)"
          }
        })
  }

  @Test
  fun supports_relationship_key_constraint() {
    verify(
        Schema::relationshipKeyConstraints,
        "CREATE CONSTRAINT c9 FOR ()-[r:Related3]->() REQUIRE r.x IS RELATIONSHIP KEY")
  }

  @Test
  fun supports_call_subquery_with_variable_scope_clause() {
    verify(
        Cypher::callSubqueryWithVariableScopeClause,
        "WITH 1 AS a CALL (a) { RETURN a AS b } RETURN b")
  }

  @Test
  fun supports_finish_clause() {
    verify(Cypher::finishClause, "MERGE (p:Person) FINISH")
  }

  private fun constraintNameOrEmpty(neo4j: Neo4j, name: String): String =
      if (canIUse(Cypher.namedConstraints()).withNeo4j(neo4j)) {
        name
      } else {
        ""
      }

  private fun verify(
      check: KFunction<Neo4jPredicate>,
      queryGenerator: (neo4j: Neo4j) -> String,
      config: SessionConfig = SessionConfig.forDatabase("neo4j")
  ) {
    val neo4j = Neo4jDetector.detect(driver)
    val query = queryGenerator.invoke(neo4j)
    return doVerify(check, neo4j, query, config)
  }

  private fun verify(
      check: KFunction<Neo4jPredicate>,
      query: String,
      config: SessionConfig = SessionConfig.forDatabase("neo4j")
  ) {
    val neo4j = Neo4jDetector.detect(driver)
    doVerify(check, neo4j, query, config)
  }

  private fun doVerify(
      check: KFunction<Neo4jPredicate>,
      neo4j: Neo4j,
      query: String,
      config: SessionConfig
  ) {
    if (canIUse(check.call()).withNeo4j(neo4j) || partiallyIntroducedBefore(check, neo4j)) {
      assertThatCode { runQuery(query, config) }.doesNotThrowAnyException()
    } else {
      assertThatThrownBy { runQuery(query, config) }.isInstanceOf(Neo4jException::class.java)
    }
  }

  private fun partiallyIntroducedBefore(check: KFunction<Neo4jPredicate>, neo4j: Neo4j): Boolean {
    val annotation = check.annotations.firstOrNull { it is PartiallyIntroducedIn }
    if (annotation == null) {
      return false
    }
    return asNeo4jPredicate(annotation as PartiallyIntroducedIn).withNeo4j(neo4j)
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
      if (canIUse(compositeDatabases()).withNeo4j(Neo4jDetector.detect(driver))) {
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
