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

/** Main entry point for Cypher-related feature detections. */
object Cypher {

  /**
   * Whether `CALL {} IN TRANSACTIONS` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun callInTransactions(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V4_4_0 }
  }

  /**
   * Whether `CALL {} IN TRANSACTIONS ON ERROR ...` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun callInTransactionsWithCustomErrorPolicy(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V5_7_0 }
  }

  /**
   * Whether `CALL {} IN TRANSACTIONS` is supported on composite databases.
   *
   * @return [Neo4jPredicate]
   */
  fun callInTransactionsWithCompositeDatabases(): Neo4jPredicate {
    return Dbms.compositeDatabases().and(Neo4jPredicate { it.version >= V5_18_0 })
  }

  /**
   * Whether `CALL {} IN CONCURRENT TRANSACTIONS` is supported.
   *
   * @return [Neo4jPredicate]
   */
  @PartiallyIntroducedIn(
      major = 5, minor = 18, description = "the syntax was supported but ignored before 5.21.0")
  fun concurrentCallInTransactions(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V5_21_0 }
  }

  /**
   * Whether `CREATE INDEX <name> ...` and `DROP INDEX <name> ...` are supported.
   *
   * @return [Neo4jPredicate]
   */
  fun namedIndexes(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V4_0_0 }
  }

  /**
   * Whether `DROP INDEX name IF EXISTS` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun dropIfExists(): Neo4jPredicate {
    return createIfNotExists()
  }

  /**
   * Whether `CREATE INDEX name IF NOT EXISTS` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun createIfNotExists(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V4_1_3 }
  }

  /**
   * Whether `SHOW INDEXES` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun showIndexes(): Neo4jPredicate {
    return showConstraints()
  }

  /**
   * Whether `SHOW CONSTRAINTS` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun showConstraints(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V4_3_0 }
  }

  /**
   * Whether `SET n:$(<expr>)` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun setDynamicLabels(): Neo4jPredicate {
    return removeDynamicPropertyKeys()
  }

  /**
   * Whether `REMOVE n:$(<expr>)` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun removeDynamicLabels(): Neo4jPredicate {
    return removeDynamicPropertyKeys()
  }

  /**
   * Whether `SET n[key] = expression` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun setDynamicPropertyKeys(): Neo4jPredicate {
    return removeDynamicPropertyKeys()
  }

  /**
   * Whether `REMOVE n[key]` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun removeDynamicPropertyKeys(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V5_24_0 }
  }

  /**
   * Whether `CREATE (n:$(<expr>))` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun createDynamicLabels(): Neo4jPredicate {
    return mergeDynamicTypes()
  }

  /**
   * Whether `MATCH (n:$(<expr>))` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun matchDynamicLabels(): Neo4jPredicate {
    return mergeDynamicTypes()
  }

  /**
   * Whether `MERGE (n:$(<expr>))` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun mergeDynamicLabels(): Neo4jPredicate {
    return mergeDynamicTypes()
  }

  /**
   * Whether `CREATE ()-[r:$(<expr>)]->()` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun createDynamicTypes(): Neo4jPredicate {
    return mergeDynamicTypes()
  }

  /**
   * Whether `MATCH ()-[r:$(<expr>)]->()` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun matchDynamicTypes(): Neo4jPredicate {
    return mergeDynamicTypes()
  }

  /**
   * Whether `MERGE ()-[r:$(<expr>)]->()` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun mergeDynamicTypes(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V5_26_0 }
  }

  /**
   * Whether `CYPHER 5` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun explicitCypher5Selection(): Neo4jPredicate {
    return explicitCypherSelection()
  }

  /**
   * Whether the general Cypher version selection clause is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun explicitCypherSelection(): Neo4jPredicate {
    // note: the documented version says 5.26.
    // Cypher 5 selection has been initially implemented as a no-op in 5.21.
    // we don't want to use @PartiallyIntroducedIn here, since that initial impl
    // does the job in that specific case anyway
    return Neo4jPredicate { it.version >= V5_21_0 }
  }
}
