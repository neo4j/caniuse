package org.neo4j.caniuse

import org.neo4j.caniuse.Versions.V4_0_0
import org.neo4j.caniuse.Versions.V4_1_3
import org.neo4j.caniuse.Versions.V4_3_0
import org.neo4j.caniuse.Versions.V4_4_0
import org.neo4j.caniuse.Versions.V5_18_0
import org.neo4j.caniuse.Versions.V5_21_0
import org.neo4j.caniuse.Versions.V5_24_0
import org.neo4j.caniuse.Versions.V5_26_0
import org.neo4j.caniuse.Versions.V5_7_0

object Cypher {

  fun callInTransactions(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V4_4_0 }
  }

  fun callInTransactionsWithCustomErrorPolicy(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V5_7_0 }
  }

  fun callInTransactionsWithCompositeDatabases(): Neo4jPredicate {
    return Dbms.compositeDatabases().and(Neo4jPredicate { it.version >= V5_18_0 })
  }

  fun concurrentCallInTransactions(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V5_21_0 }
  }

  fun namedIndexes(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V4_0_0 }
  }

  fun dropIfExists(): Neo4jPredicate {
    return createIfNotExists()
  }

  fun createIfNotExists(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V4_1_3 }
  }

  fun showIndexes(): Neo4jPredicate {
    return showConstraints()
  }

  fun showConstraints(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V4_3_0 }
  }

  fun setDynamicLabels(): Neo4jPredicate {
    return removeDynamicPropertyKeys()
  }

  fun removeDynamicLabels(): Neo4jPredicate {
    return removeDynamicPropertyKeys()
  }

  fun setDynamicPropertyKeys(): Neo4jPredicate {
    return removeDynamicPropertyKeys()
  }

  fun removeDynamicPropertyKeys(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V5_24_0 }
  }

  fun createDynamicLabels(): Neo4jPredicate {
    return mergeDynamicTypes()
  }

  fun matchDynamicLabels(): Neo4jPredicate {
    return mergeDynamicTypes()
  }

  fun mergeDynamicLabels(): Neo4jPredicate {
    return mergeDynamicTypes()
  }

  fun createDynamicTypes(): Neo4jPredicate {
    return mergeDynamicTypes()
  }

  fun matchDynamicTypes(): Neo4jPredicate {
    return mergeDynamicTypes()
  }

  fun mergeDynamicTypes(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V5_26_0 }
  }

  fun explicitCypher5Selection(): Neo4jPredicate {
    return explicitCypherSelection()
  }

  fun explicitCypherSelection(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V5_26_0 }
  }
}
