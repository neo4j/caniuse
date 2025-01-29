package com.neo4j.data.importer;

import static com.neo4j.data.importer.Versions.V5_10_0;
import static com.neo4j.data.importer.Versions.V5_11_0;
import static com.neo4j.data.importer.Versions.V5_13_0;
import static com.neo4j.data.importer.Versions.V5_9_0;

public class Schema {

    private Schema() {}

    public static Neo4jPredicate propertyTypeConstraints() {
        return new Neo4jPredicate((neo4j) ->
                neo4j.edition() == Neo4jEdition.ENTERPRISE && neo4j.version().greaterThanOrEqual(V5_9_0));
    }

    public static Neo4jPredicate propertyListTypeConstraints() {
        return new Neo4jPredicate((neo4j) ->
                neo4j.edition() == Neo4jEdition.ENTERPRISE && neo4j.version().greaterThanOrEqual(V5_10_0));
    }

    public static Neo4jPredicate propertyUnionTypeConstraints() {
        return new Neo4jPredicate((neo4j) ->
                neo4j.edition() == Neo4jEdition.ENTERPRISE && neo4j.version().greaterThanOrEqual(V5_11_0));
    }

    public static Neo4jPredicate vectorIndexes() {
        // beta in 5.11, GA in 5.13
        return new Neo4jPredicate((neo4j) -> neo4j.version().greaterThanOrEqual(V5_13_0));
    }
}
