package org.neo4j.caniuse

import org.neo4j.caniuse.Versions.V5_10_0
import org.neo4j.caniuse.Versions.V5_11_0
import org.neo4j.caniuse.Versions.V5_13_0
import org.neo4j.caniuse.Versions.V5_9_0

/** Main entry point for schema-related feature detections. */
object Schema {

    /**
     * Whether property type constraints are supported.
     *
     * @return [Neo4jPredicate]
     */
    fun propertyTypeConstraints(): Neo4jPredicate {
        return Neo4jPredicate { neo4j: Neo4j -> neo4j.edition === Neo4jEdition.ENTERPRISE && neo4j.version >= V5_9_0 }
    }

    /**
     * Whether property list type constraints are supported.
     *
     * @return [Neo4jPredicate]
     */
    fun propertyListTypeConstraints(): Neo4jPredicate {
        return Neo4jPredicate { neo4j: Neo4j -> neo4j.edition === Neo4jEdition.ENTERPRISE && neo4j.version >= V5_10_0 }
    }

    /**
     * Whether property union type constraints are supported.
     *
     * @return [Neo4jPredicate]
     */
    fun propertyUnionTypeConstraints(): Neo4jPredicate {
        return Neo4jPredicate { neo4j: Neo4j -> neo4j.edition === Neo4jEdition.ENTERPRISE && neo4j.version >= V5_11_0 }
    }

    /**
     * Whether vector indexes are supported
     *
     * @return [Neo4jPredicate]
     */
    fun vectorIndexes(): Neo4jPredicate {
        // beta in 5.11, GA in 5.13
        return Neo4jPredicate { neo4j: Neo4j -> neo4j.version >= V5_13_0 }
    }
}
