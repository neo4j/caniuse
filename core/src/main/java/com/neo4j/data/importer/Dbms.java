package com.neo4j.data.importer;

import static com.neo4j.data.importer.Versions.V4_0_0;
import static com.neo4j.data.importer.Versions.V5_0_0;

public class Dbms {

    private Dbms() {}

    public static Neo4jPredicate multiDatabase() {
        return new Neo4jPredicate((neo4j) -> neo4j.edition() == Neo4jEdition.ENTERPRISE
                && neo4j.version().greaterThanOrEqual(V4_0_0)
                && neo4j.environment() != Neo4jEnvironment.AURA);
    }

    public static Neo4jPredicate compositeDatabases() {
        return new Neo4jPredicate((neo4j) -> neo4j.edition() == Neo4jEdition.ENTERPRISE
                && neo4j.version().greaterThanOrEqual(V5_0_0)
                && neo4j.environment() != Neo4jEnvironment.AURA);
    }
}
