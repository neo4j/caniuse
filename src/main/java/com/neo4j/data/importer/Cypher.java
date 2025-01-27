package com.neo4j.data.importer;

import static com.neo4j.data.importer.Neo4jVersion.V4_0_0;
import static com.neo4j.data.importer.Neo4jVersion.V4_1_3;
import static com.neo4j.data.importer.Neo4jVersion.V4_3_0;
import static com.neo4j.data.importer.Neo4jVersion.V4_4_0;
import static com.neo4j.data.importer.Neo4jVersion.V5_18_0;
import static com.neo4j.data.importer.Neo4jVersion.V5_21_0;
import static com.neo4j.data.importer.Neo4jVersion.V5_24_0;
import static com.neo4j.data.importer.Neo4jVersion.V5_26_0;
import static com.neo4j.data.importer.Neo4jVersion.V5_7_0;

public class Cypher {

    private Cypher() {}

    public static Neo4jPredicate callInTransactions() {
        return new Neo4jPredicate((neo4j) -> neo4j.version().greaterThanOrEqual(V4_4_0));
    }

    public static Neo4jPredicate callInTransactionsWithCustomErrorPolicy() {
        return new Neo4jPredicate((neo4j) -> neo4j.version().greaterThanOrEqual(V5_7_0));
    }

    public static Neo4jPredicate callInTransactionsWithCompositeDatabases() {
        return Dbms.compositeDatabases()
                .and(new Neo4jPredicate((neo4j) -> neo4j.version().greaterThanOrEqual(V5_18_0)));
    }

    public static Neo4jPredicate concurrentCallInTransactions() {
        return new Neo4jPredicate((neo4j) -> neo4j.version().greaterThanOrEqual(V5_21_0));
    }

    public static Neo4jPredicate namedIndexes() {
        return new Neo4jPredicate((neo4j) -> neo4j.version().greaterThanOrEqual(V4_0_0));
    }

    public static Neo4jPredicate dropIfExists() {
        return createIfNotExists();
    }

    public static Neo4jPredicate createIfNotExists() {
        return new Neo4jPredicate((neo4j) -> neo4j.version().greaterThanOrEqual(V4_1_3));
    }

    public static Neo4jPredicate showIndexes() {
        return showConstraints();
    }

    public static Neo4jPredicate showConstraints() {
        return new Neo4jPredicate((neo4j) -> neo4j.version().greaterThanOrEqual(V4_3_0));
    }

    public static Neo4jPredicate setDynamicLabels() {
        return removeDynamicPropertyKeys();
    }

    public static Neo4jPredicate removeDynamicLabels() {
        return removeDynamicPropertyKeys();
    }

    public static Neo4jPredicate setDynamicPropertyKeys() {
        return removeDynamicPropertyKeys();
    }

    public static Neo4jPredicate removeDynamicPropertyKeys() {
        return new Neo4jPredicate((neo4j) -> neo4j.version().greaterThanOrEqual(V5_24_0));
    }

    public static Neo4jPredicate createDynamicLabels() {
        return mergeDynamicTypes();
    }

    public static Neo4jPredicate matchDynamicLabels() {
        return mergeDynamicTypes();
    }

    public static Neo4jPredicate mergeDynamicLabels() {
        return mergeDynamicTypes();
    }

    public static Neo4jPredicate createDynamicTypes() {
        return mergeDynamicTypes();
    }

    public static Neo4jPredicate matchDynamicTypes() {
        return mergeDynamicTypes();
    }

    public static Neo4jPredicate mergeDynamicTypes() {
        return new Neo4jPredicate((neo4j) -> neo4j.version().greaterThanOrEqual(V5_26_0));
    }
}
