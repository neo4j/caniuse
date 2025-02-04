package com.neo4j.data.importer

import java.util.function.Predicate

class Neo4jPredicate(private val predicate: Predicate<Neo4j>) {

  fun withNeo4j(neo4j: Neo4j): Boolean {
    return predicate.test(neo4j)
  }

  fun and(other: Neo4jPredicate): Neo4jPredicate {
    return Neo4jPredicate(predicate.and(other.predicate))
  }
}
