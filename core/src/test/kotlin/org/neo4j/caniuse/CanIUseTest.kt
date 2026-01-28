package org.neo4j.caniuse

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.aggregator.AggregateWith
import org.junit.jupiter.params.provider.CsvSource
import org.neo4j.caniuse.CanIUse.canIUse
import org.neo4j.caniuse.Cypher.callInTransactions
import org.neo4j.caniuse.Cypher.callInTransactionsWithCompositeDatabases
import org.neo4j.caniuse.Cypher.callInTransactionsWithCustomErrorPolicy
import org.neo4j.caniuse.Cypher.concurrentCallInTransactions
import org.neo4j.caniuse.Cypher.constraintsWithRequireKeyword
import org.neo4j.caniuse.Cypher.createDynamicLabels
import org.neo4j.caniuse.Cypher.createDynamicTypes
import org.neo4j.caniuse.Cypher.createIfNotExists
import org.neo4j.caniuse.Cypher.dropIfExists
import org.neo4j.caniuse.Cypher.dynamicLabelsAndTypesCanLeveragePropertyIndices
import org.neo4j.caniuse.Cypher.explicitCypher25Selection
import org.neo4j.caniuse.Cypher.explicitCypher5Selection
import org.neo4j.caniuse.Cypher.explicitCypherSelection
import org.neo4j.caniuse.Cypher.matchDynamicLabels
import org.neo4j.caniuse.Cypher.matchDynamicTypes
import org.neo4j.caniuse.Cypher.mergeDynamicLabels
import org.neo4j.caniuse.Cypher.mergeDynamicTypes
import org.neo4j.caniuse.Cypher.namedConstraints
import org.neo4j.caniuse.Cypher.namedIndexes
import org.neo4j.caniuse.Cypher.removeDynamicLabels
import org.neo4j.caniuse.Cypher.removeDynamicPropertyKeys
import org.neo4j.caniuse.Cypher.setDynamicLabels
import org.neo4j.caniuse.Cypher.setDynamicPropertyKeys
import org.neo4j.caniuse.Cypher.showConstraints
import org.neo4j.caniuse.Cypher.showIndexes
import org.neo4j.caniuse.Dbms.changeDataCapture
import org.neo4j.caniuse.Dbms.compositeDatabases
import org.neo4j.caniuse.Dbms.multiDatabase
import org.neo4j.caniuse.Schema.nodeKeyConstraints
import org.neo4j.caniuse.Schema.nodePropertyExistenceConstraints
import org.neo4j.caniuse.Schema.nodePropertyUniquenessConstraints
import org.neo4j.caniuse.Schema.propertyListTypeConstraints
import org.neo4j.caniuse.Schema.propertyTypeConstraints
import org.neo4j.caniuse.Schema.propertyUnionTypeConstraints
import org.neo4j.caniuse.Schema.relationshipKeyConstraints
import org.neo4j.caniuse.Schema.relationshipPropertyExistenceConstraints
import org.neo4j.caniuse.Schema.relationshipPropertyUniquenessConstraints
import org.neo4j.caniuse.Schema.vectorIndexes

internal class CanIUseTest {
  @CsvSource(
      "false,community,4,3", "false,enterprise,4,3", "true,community,4,4", "true,enterprise,4,4")
  @ParameterizedTest
  fun supports_calls_in_transactions(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(callInTransactions()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,4,4",
      "false,enterprise,4,4",
      "false,community,5,0",
      "false,enterprise,5,0",
      "true,community,5,7",
      "true,enterprise,5,7")
  @ParameterizedTest
  fun supports_calls_in_transactions_with_custom_error_policy(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(callInTransactionsWithCustomErrorPolicy()).withNeo4j(neo4j))
        .isEqualTo(result)
  }

  @CsvSource(
      "false,community,4,4",
      "false,enterprise,4,4",
      "false,community,5,0",
      "false,enterprise,5,0",
      "false,community,5,18",
      "true,enterprise,5,18")
  @ParameterizedTest
  fun supports_calls_in_transactions_with_composite_databases(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(callInTransactionsWithCompositeDatabases()).withNeo4j(neo4j))
        .isEqualTo(result)
  }

  @CsvSource(
      "false,community,4,4",
      "false,enterprise,4,4",
      "false,community,5,0",
      "false,enterprise,5,0",
      "false,community,5,18",
      "false,enterprise,5,18")
  @ParameterizedTest
  fun supports_calls_in_transactions_with_composite_databases_in_aura(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    val neo4jAura: Neo4j = neo4j.copy(deploymentType = Neo4jDeploymentType.AURA)

    assertThat(canIUse(callInTransactionsWithCompositeDatabases()).withNeo4j(neo4jAura))
        .isEqualTo(result)
  }

  @CsvSource(
      "false,community,4,4",
      "false,enterprise,4,4",
      "false,community,5,0",
      "false,enterprise,5,0",
      "true,community,5,21",
      "true,enterprise,5,21")
  @ParameterizedTest
  fun supports_concurrent_calls_in_transactions(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(concurrentCallInTransactions()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,3,5", "false,enterprise,3,5", "true,community,4,0", "true,enterprise,4,0")
  @ParameterizedTest
  fun supports_named_indexes(result: Boolean, @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j) {
    assertThat(canIUse(namedIndexes()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,3,5", "false,enterprise,3,5", "true,community,4,0", "true,enterprise,4,0")
  @ParameterizedTest
  fun supports_named_constraints(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(namedConstraints()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,3,5", "false,enterprise,3,5", "true,community,4,4", "true,enterprise,4,4")
  @ParameterizedTest
  fun supports_constraints_with_require_keyword(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(constraintsWithRequireKeyword()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,3,5",
      "false,enterprise,3,5",
      "false,community,4,0",
      "false,enterprise,4,0",
      "true,community,4,1,3",
      "true,enterprise,4,1,3")
  @ParameterizedTest
  fun supports_drop_if_exists(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(dropIfExists()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,3,5",
      "false,enterprise,3,5",
      "false,community,4,0",
      "false,enterprise,4,0",
      "true,community,4,1,3",
      "true,enterprise,4,1,3")
  @ParameterizedTest
  fun supports_create_if_not_exists(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(createIfNotExists()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,3,5", "false,enterprise,3,5", "false,community,4,0", "true,enterprise,4,0")
  @ParameterizedTest
  fun supports_multi_database_on_prem(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(multiDatabase()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,4,4", "false,enterprise,4,4", "false,community,5,0", "true,enterprise,5,0")
  @ParameterizedTest
  fun supports_composite_databases(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(compositeDatabases()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,3,5", "false,enterprise,3,5", "false,community,4,0", "false,enterprise,4,0")
  @ParameterizedTest
  fun supports_multi_database_on_Aura(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    val neo4jAura: Neo4j = neo4j.copy(deploymentType = Neo4jDeploymentType.AURA)

    assertThat(canIUse(multiDatabase()).withNeo4j(neo4jAura)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,4,4", "false,enterprise,4,4", "false,community,5,0", "false,enterprise,5,0")
  @ParameterizedTest
  fun supports_composite_databases_on_Aura(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    val neo4jAura: Neo4j = neo4j.copy(deploymentType = Neo4jDeploymentType.AURA)

    assertThat(canIUse(compositeDatabases()).withNeo4j(neo4jAura)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,3,5",
      "false,enterprise,3,5",
      "false,community,4,0",
      "false,enterprise,4,0",
      "true,community,4,3",
      "true,enterprise,4,3")
  @ParameterizedTest
  fun supports_show_indexes(result: Boolean, @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j) {
    assertThat(canIUse(showIndexes()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,3,5",
      "false,enterprise,3,5",
      "false,community,4,0",
      "false,enterprise,4,0",
      "true,community,4,3",
      "true,enterprise,4,3")
  @ParameterizedTest
  fun supports_show_constraints(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(showConstraints()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "true,community,5,24", "true,enterprise,5,24")
  @ParameterizedTest
  fun supports_set_dynamic_labels(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(setDynamicLabels()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "true,community,5,24", "true,enterprise,5,24")
  @ParameterizedTest
  fun supports_remove_dynamic_labels(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(removeDynamicLabels()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "true,community,5,24", "true,enterprise,5,24")
  @ParameterizedTest
  fun supports_set_dynamic_property_keys(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(setDynamicPropertyKeys()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "true,community,5,24", "true,enterprise,5,24")
  @ParameterizedTest
  fun supports_remove_dynamic_property_keys(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(removeDynamicPropertyKeys()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "true,community,5,26", "true,enterprise,5,26")
  @ParameterizedTest
  fun supports_create_dynamic_labels(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(createDynamicLabels()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "true,community,5,26", "true,enterprise,5,26")
  @ParameterizedTest
  fun supports_match_dynamic_labels(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(matchDynamicLabels()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "true,community,5,26", "true,enterprise,5,26")
  @ParameterizedTest
  fun supports_merge_dynamic_labels(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(mergeDynamicLabels()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "true,community,5,26", "true,enterprise,5,26")
  @ParameterizedTest
  fun supports_create_dynamic_types(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(createDynamicTypes()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "true,community,5,26", "true,enterprise,5,26")
  @ParameterizedTest
  fun supports_match_dynamic_types(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(matchDynamicTypes()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "true,community,5,26", "true,enterprise,5,26")
  @ParameterizedTest
  fun supports_merge_dynamic_types(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(mergeDynamicTypes()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "false,community,5,9", "true,enterprise,5,9")
  @ParameterizedTest
  fun supports_property_type_constraints(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(propertyTypeConstraints()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "false,community,5,10", "true,enterprise,5,10")
  @ParameterizedTest
  fun supports_property_list_type_constraints(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(propertyListTypeConstraints()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "false,community,5,11", "true,enterprise,5,11")
  @ParameterizedTest
  fun supports_property_union_type_constraints(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(propertyUnionTypeConstraints()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0", "false,enterprise,5,0", "true,community,5,13", "true,enterprise,5,13")
  @ParameterizedTest
  fun supports_vector_indexes(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(vectorIndexes()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0",
      "false,enterprise,5,0",
      "true,community,5,26",
      "true,enterprise,5,26",
      "true,community,2025,1",
      "true,enterprise,2025,1")
  @ParameterizedTest
  fun supports_cypher_explicit_version_selection(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(explicitCypherSelection()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0",
      "false,enterprise,5,0",
      "true,community,5,26",
      "true,enterprise,5,26",
      "true,community,2025,1",
      "true,enterprise,2025,1")
  @ParameterizedTest
  fun supports_cypher_version_5_selection(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(explicitCypher5Selection()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,5,0",
      "false,enterprise,5,0",
      "false,community,5,26",
      "false,enterprise,5,26",
      "false,community,2025,1",
      "false,enterprise,2025,1",
      "true,community,2025,7",
      "true,enterprise,2025,7",
      "true,enterprise,2026,1",
      "true,aura,5,27",
      "false,aura,4,4",
  )
  @ParameterizedTest
  fun supports_cypher_version_25_selection(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(explicitCypher25Selection()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,4,4",
      "false,enterprise,4,4",
      "false,community,5,0",
      "false,enterprise,5,0",
      "false,community,5,23",
      "true,enterprise,5,23",
      "false,community,2025,1",
      "true,enterprise,2025,1")
  @ParameterizedTest
  fun supports_change_data_capture(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(changeDataCapture()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,2,0",
      "false,enterprise,2,0",
      "true,community,4,3",
      "true,enterprise,4,3",
      "true,community,5,26",
      "true,enterprise,5,26",
      "true,community,2025,1",
      "true,enterprise,2025,1")
  @ParameterizedTest
  fun supports_node_property_uniqueness_constraints(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(nodePropertyUniquenessConstraints()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,2,0",
      "false,enterprise,2,0",
      "false,community,4,3",
      "true,enterprise,4,3",
      "false,community,5,26",
      "true,enterprise,5,26",
      "false,community,2025,1",
      "true,enterprise,2025,1")
  @ParameterizedTest
  fun supports_node_property_existence_constraints(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(nodePropertyExistenceConstraints()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,2,0",
      "false,enterprise,2,0",
      "false,community,4,3",
      "true,enterprise,4,3",
      "false,community,5,26",
      "true,enterprise,5,26",
      "false,community,2025,1",
      "true,enterprise,2025,1")
  @ParameterizedTest
  fun supports_node_key_constraints(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(nodeKeyConstraints()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,4,3",
      "false,enterprise,4,3",
      "false,community,5,5",
      "false,enterprise,5,5",
      "true,enterprise,5,7",
      "true,community,5,26",
      "true,enterprise,5,26",
      "true,community,2025,1",
      "true,enterprise,2025,1")
  @ParameterizedTest
  fun supports_relationship_property_uniqueness_constraints(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(relationshipPropertyUniquenessConstraints()).withNeo4j(neo4j))
        .isEqualTo(result)
  }

  @CsvSource(
      "false,community,2,0",
      "false,enterprise,2,0",
      "false,community,4,3",
      "true,enterprise,4,3",
      "false,community,5,26",
      "true,enterprise,5,26",
      "false,community,2025,1",
      "true,enterprise,2025,1")
  @ParameterizedTest
  fun supports_relationship_property_existence_constraints(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(relationshipPropertyExistenceConstraints()).withNeo4j(neo4j))
        .isEqualTo(result)
  }

  @CsvSource(
      "false,community,4,3",
      "false,enterprise,4,3",
      "false,community,5,5",
      "false,enterprise,5,5",
      "true,enterprise,5,7",
      "false,community,5,26",
      "true,enterprise,5,26",
      "false,community,2025,1",
      "true,enterprise,2025,1")
  @ParameterizedTest
  fun supports_relationship_key_constraints(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    assertThat(canIUse(relationshipKeyConstraints()).withNeo4j(neo4j)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,4,3",
      "false,enterprise,4,3",
      "false,community,5,5",
      "false,enterprise,5,5",
      "false,community,5,26",
      "false,enterprise,5,26",
      "false,community,2025,1",
      "false,community,2025,10",
      "true,enterprise,2025,11",
      "true,enterprise,2025,12",
      "false,aura,5,26",
      "true,aura,5,27",
      "true,aura,2025,11",
      "true,aura,2026,1",
  )
  @ParameterizedTest
  fun supports_dynamic_labels_and_types_can_leverage_property_indices(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j,
  ) {
    assertThat(canIUse(dynamicLabelsAndTypesCanLeveragePropertyIndices()).withNeo4j(neo4j))
        .isEqualTo(result)
  }
}
