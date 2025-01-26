package com.neo4j.data.importer;

import org.neo4j.driver.Driver;

public class Neo4jSupport {

    private final Neo4j neo4j;

    public static Neo4jSupport detectedNeo4jSupports(Driver driver) {
        return Neo4jSupport.neo4jSupports(Neo4jDetector.detect(driver));
    }

    public static Neo4jSupport neo4jSupports(Neo4j neo4j) {
        return new Neo4jSupport(neo4j);
    }

    public Neo4jSupport(Neo4j neo4j) {
        this.neo4j = neo4j;
    }

    public CypherSupport cypher() {
        return new CypherSupport(neo4j);
    }
}
