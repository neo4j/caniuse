package com.neo4j.data.importer;

public class Dbms {

    private Dbms() {}

    public static Neo4jPredicate multiDatabase() {
        return new Neo4jPredicate((neo4j) -> neo4j.edition() == Neo4jEdition.ENTERPRISE
                && neo4j.version().greaterThanOrEqual(Neo4jVersion.V4_0_0)
                && neo4j.environment() != Neo4jEnvironment.AURA);
    }

    public static Neo4jPredicate compositeDatabases() {
        return new Neo4jPredicate((neo4j) -> neo4j.edition() == Neo4jEdition.ENTERPRISE
                && neo4j.version().greaterThanOrEqual(Neo4jVersion.V5_0_0)
                && neo4j.environment() != Neo4jEnvironment.AURA);
    }
}
