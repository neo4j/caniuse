package com.neo4j.data.importer;

import static com.neo4j.data.importer.Neo4jVersion.V5_0_0;
import static com.neo4j.data.importer.Neo4jVersion.V5_18_0;
import static com.neo4j.data.importer.Neo4jVersion.V5_21_0;
import static com.neo4j.data.importer.Neo4jVersion.V5_7_0;

public class CypherSupport {

    private final Neo4j neo4j;

    CypherSupport(Neo4j neo4j) {
        this.neo4j = neo4j;
    }

    public boolean callInTransactions() {
        return neo4j.version().greaterThanOrEqual(V5_0_0);
    }

    public boolean callInTransactionsWithCustomErrorPolicy() {
        return neo4j.version().greaterThanOrEqual(V5_7_0);
    }

    public boolean callInTransactionsWithCompositeDatabases() {
        return neo4j.version().greaterThanOrEqual(V5_18_0);
    }

    public boolean concurrentCallInTransactions() {
        return neo4j.version().greaterThanOrEqual(V5_21_0);
    }
}
