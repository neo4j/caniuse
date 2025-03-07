package org.neo4j.caniuse

/** Main class for feature detections. */
object CanIUse {

    /**
     * Entry for running feature detection against a particular Neo4j target.
     *
     * @return the specified [Neo4jPredicate]
     */
    fun canIUse(predicate: Neo4jPredicate): Neo4jPredicate {
        return predicate
    }
}
