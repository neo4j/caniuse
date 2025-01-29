package com.neo4j.data.importer;

import java.util.function.Predicate;

public class Neo4jPredicate {

    private final Predicate<Neo4j> predicate;

    Neo4jPredicate(Predicate<Neo4j> predicate) {
        this.predicate = predicate;
    }

    public boolean withNeo4j(Neo4j neo4j) {
        return predicate.test(neo4j);
    }

    public Neo4jPredicate and(Neo4jPredicate other) {
        return new Neo4jPredicate(predicate.and(other.predicate));
    }
}
