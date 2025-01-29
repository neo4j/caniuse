package com.neo4j.data.importer.example;

import static com.neo4j.data.importer.CanIUse.canIUse;
import static com.neo4j.data.importer.Neo4jEdition.ENTERPRISE;
import static com.neo4j.data.importer.Neo4jEnvironment.ON_PREMISE;

import com.neo4j.data.importer.Cypher;
import com.neo4j.data.importer.Neo4j;
import com.neo4j.data.importer.Neo4jVersion;

public class Example {

    public static void main(String[] args) {
        Neo4j neo4j = Neo4j.builder()
                .edition(ENTERPRISE)
                .environment(ON_PREMISE)
                .version(Neo4jVersion.of(2025, 1))
                .build();
        if (canIUse(Cypher.concurrentCallInTransactions()).withNeo4j(neo4j)) {
            // run concurrent CALL IN TRANSACTIONS \o/
        }
    }
}
