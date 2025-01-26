package com.neo4j.data.importer;

import java.util.function.Predicate;
import org.neo4j.driver.Driver;

public class Neo4jPredicate {

    private final Predicate<Neo4j> predicate;

    Neo4jPredicate(Predicate<Neo4j> predicate) {
        this.predicate = predicate;
    }

    public boolean withNeo4j(Neo4j neo4j) {
        return predicate.test(neo4j);
    }

    public boolean withDetectedNeo4j(Driver driver) {
        return predicate.test(Neo4jDetector.detect(driver));
    }
}
