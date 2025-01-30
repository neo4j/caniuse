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
import com.neo4j.data.importer.Cypher.matchDynamicLabels
import com.neo4j.data.importer.Cypher.matchDynamicTypes
import com.neo4j.data.importer.Cypher.mergeDynamicLabels
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
import com.neo4j.data.importer.Schema.vectorIndexes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.aggregator.AggregateWith
import org.junit.jupiter.params.provider.CsvSource

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
    val neo4jAura: Neo4j = neo4j.copy(environment = Neo4jEnvironment.AURA)

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
    val neo4jAura: Neo4j = neo4j.copy(environment = Neo4jEnvironment.AURA)

    assertThat(canIUse(multiDatabase()).withNeo4j(neo4jAura)).isEqualTo(result)
  }

  @CsvSource(
      "false,community,4,4", "false,enterprise,4,4", "false,community,5,0", "false,enterprise,5,0")
  @ParameterizedTest
  fun supports_composite_databases_on_Aura(
      result: Boolean,
      @AggregateWith(Neo4jAggregator::class) neo4j: Neo4j
  ) {
    val neo4jAura: Neo4j = neo4j.copy(environment = Neo4jEnvironment.AURA)

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
}
