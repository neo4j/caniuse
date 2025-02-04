package com.neo4j.data.importer

import com.neo4j.data.importer.Versions.V5_10_0
import com.neo4j.data.importer.Versions.V5_11_0
import com.neo4j.data.importer.Versions.V5_13_0
import com.neo4j.data.importer.Versions.V5_9_0

object Schema {
  fun propertyTypeConstraints(): Neo4jPredicate {
    return Neo4jPredicate { neo4j: Neo4j ->
      neo4j.edition === Neo4jEdition.ENTERPRISE && neo4j.version >= V5_9_0
    }
  }

  fun propertyListTypeConstraints(): Neo4jPredicate {
    return Neo4jPredicate { neo4j: Neo4j ->
      neo4j.edition === Neo4jEdition.ENTERPRISE && neo4j.version >= V5_10_0
    }
  }

  fun propertyUnionTypeConstraints(): Neo4jPredicate {
    return Neo4jPredicate { neo4j: Neo4j ->
      neo4j.edition === Neo4jEdition.ENTERPRISE && neo4j.version >= V5_11_0
    }
  }

  fun vectorIndexes(): Neo4jPredicate {
    // beta in 5.11, GA in 5.13
    return Neo4jPredicate { neo4j: Neo4j -> neo4j.version >= V5_13_0 }
  }
}
