package com.neo4j.data.importer;

import static com.neo4j.data.importer.CanIUse.canIUse;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.CsvSource;

class CanIUseTest {

    @CsvSource({"false,community,4,3", "false,enterprise,4,3", "true,community,4,4", "true,enterprise,4,4"})
    @ParameterizedTest
    void supports_calls_in_transactions(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.callInTransactions()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,4,4",
        "false,enterprise,4,4",
        "false,community,5,0",
        "false,enterprise,5,0",
        "true,community,5,7",
        "true,enterprise,5,7"
    })
    @ParameterizedTest
    void supports_calls_in_transactions_with_custom_error_policy(
            boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.callInTransactionsWithCustomErrorPolicy()).withNeo4j(neo4j))
                .isEqualTo(result);
    }

    @CsvSource({
        "false,community,4,4",
        "false,enterprise,4,4",
        "false,community,5,0",
        "false,enterprise,5,0",
        "false,community,5,18",
        "true,enterprise,5,18"
    })
    @ParameterizedTest
    void supports_calls_in_transactions_with_composite_databases(
            boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.callInTransactionsWithCompositeDatabases()).withNeo4j(neo4j))
                .isEqualTo(result);
    }

    @CsvSource({
        "false,community,4,4",
        "false,enterprise,4,4",
        "false,community,5,0",
        "false,enterprise,5,0",
        "false,community,5,18",
        "false,enterprise,5,18"
    })
    @ParameterizedTest
    void supports_calls_in_transactions_with_composite_databases_in_aura(
            boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        Neo4j neo4jAura =
                Neo4j.builder(neo4j).environment(Neo4jEnvironment.AURA).build();

        assertThat(canIUse(Cypher.callInTransactionsWithCompositeDatabases()).withNeo4j(neo4jAura))
                .isEqualTo(result);
    }

    @CsvSource({
        "false,community,4,4",
        "false,enterprise,4,4",
        "false,community,5,0",
        "false,enterprise,5,0",
        "true,community,5,21",
        "true,enterprise,5,21"
    })
    @ParameterizedTest
    void supports_concurrent_calls_in_transactions(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.concurrentCallInTransactions()).withNeo4j(neo4j))
                .isEqualTo(result);
    }

    @CsvSource({"false,community,3,5", "false,enterprise,3,5", "true,community,4,0", "true,enterprise,4,0"})
    @ParameterizedTest
    void supports_named_indexes(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.namedIndexes()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,3,5",
        "false,enterprise,3,5",
        "false,community,4,0",
        "false,enterprise,4,0",
        "true,community,4,1,3",
        "true,enterprise,4,1,3",
    })
    @ParameterizedTest
    void supports_drop_if_exists(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.dropIfExists()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,3,5",
        "false,enterprise,3,5",
        "false,community,4,0",
        "false,enterprise,4,0",
        "true,community,4,1,3",
        "true,enterprise,4,1,3",
    })
    @ParameterizedTest
    void supports_create_if_not_exists(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.createIfNotExists()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,3,5",
        "false,enterprise,3,5",
        "false,community,4,0",
        "true,enterprise,4,0",
    })
    @ParameterizedTest
    void supports_multi_database_on_prem(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Dbms.multiDatabase()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,4,4",
        "false,enterprise,4,4",
        "false,community,5,0",
        "true,enterprise,5,0",
    })
    @ParameterizedTest
    void supports_composite_databases(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Dbms.compositeDatabases()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,3,5",
        "false,enterprise,3,5",
        "false,community,4,0",
        "false,enterprise,4,0",
    })
    @ParameterizedTest
    void supports_multi_database_on_Aura(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        Neo4j neo4jAura =
                Neo4j.builder(neo4j).environment(Neo4jEnvironment.AURA).build();

        assertThat(canIUse(Dbms.multiDatabase()).withNeo4j(neo4jAura)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,4,4",
        "false,enterprise,4,4",
        "false,community,5,0",
        "false,enterprise,5,0",
    })
    @ParameterizedTest
    void supports_composite_databases_on_Aura(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        Neo4j neo4jAura =
                Neo4j.builder(neo4j).environment(Neo4jEnvironment.AURA).build();

        assertThat(canIUse(Dbms.compositeDatabases()).withNeo4j(neo4jAura)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,3,5",
        "false,enterprise,3,5",
        "false,community,4,0",
        "false,enterprise,4,0",
        "true,community,4,3",
        "true,enterprise,4,3",
    })
    @ParameterizedTest
    void supports_show_indexes(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.showIndexes()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,3,5",
        "false,enterprise,3,5",
        "false,community,4,0",
        "false,enterprise,4,0",
        "true,community,4,3",
        "true,enterprise,4,3",
    })
    @ParameterizedTest
    void supports_show_constraints(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.showConstraints()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "true,community,5,24",
        "true,enterprise,5,24",
    })
    @ParameterizedTest
    void supports_set_dynamic_labels(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.setDynamicLabels()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "true,community,5,24",
        "true,enterprise,5,24",
    })
    @ParameterizedTest
    void supports_remove_dynamic_labels(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.removeDynamicLabels()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "true,community,5,24",
        "true,enterprise,5,24",
    })
    @ParameterizedTest
    void supports_set_dynamic_property_keys(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.setDynamicPropertyKeys()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "true,community,5,24",
        "true,enterprise,5,24",
    })
    @ParameterizedTest
    void supports_remove_dynamic_property_keys(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.removeDynamicPropertyKeys()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "true,community,5,26",
        "true,enterprise,5,26",
    })
    @ParameterizedTest
    void supports_create_dynamic_labels(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.createDynamicLabels()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "true,community,5,26",
        "true,enterprise,5,26",
    })
    @ParameterizedTest
    void supports_match_dynamic_labels(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.matchDynamicLabels()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "true,community,5,26",
        "true,enterprise,5,26",
    })
    @ParameterizedTest
    void supports_merge_dynamic_labels(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.mergeDynamicLabels()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "true,community,5,26",
        "true,enterprise,5,26",
    })
    @ParameterizedTest
    void supports_create_dynamic_types(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.createDynamicTypes()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "true,community,5,26",
        "true,enterprise,5,26",
    })
    @ParameterizedTest
    void supports_match_dynamic_types(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.matchDynamicTypes()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "true,community,5,26",
        "true,enterprise,5,26",
    })
    @ParameterizedTest
    void supports_merge_dynamic_types(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Cypher.mergeDynamicTypes()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "false,community,5,9",
        "true,enterprise,5,9",
    })
    @ParameterizedTest
    void supports_property_type_constraints(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Schema.propertyTypeConstraints()).withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "false,community,5,10",
        "true,enterprise,5,10",
    })
    @ParameterizedTest
    void supports_property_list_type_constraints(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Schema.propertyListTypeConstraints()).withNeo4j(neo4j))
                .isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "false,community,5,11",
        "true,enterprise,5,11",
    })
    @ParameterizedTest
    void supports_property_union_type_constraints(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Schema.propertyUnionTypeConstraints()).withNeo4j(neo4j))
                .isEqualTo(result);
    }

    @CsvSource({
        "false,community,5,0",
        "false,enterprise,5,0",
        "true,community,5,13",
        "true,enterprise,5,13",
    })
    @ParameterizedTest
    void supports_vector_indexes(boolean result, @AggregateWith(Neo4jAggregator.class) Neo4j neo4j) {
        assertThat(canIUse(Schema.vectorIndexes()).withNeo4j(neo4j)).isEqualTo(result);
    }
}
