/*
 * Copyright (c) "Neo4j"
 * Neo4j Sweden AB [https://neo4j.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.caniuse

import org.neo4j.caniuse.Versions.V2025_6_0
import org.neo4j.caniuse.Versions.V2026_01_4
import org.neo4j.caniuse.Versions.V4_0_0
import org.neo4j.caniuse.Versions.V4_1_3
import org.neo4j.caniuse.Versions.V4_3_0
import org.neo4j.caniuse.Versions.V4_4_0
import org.neo4j.caniuse.Versions.V5_18_0
import org.neo4j.caniuse.Versions.V5_19_0
import org.neo4j.caniuse.Versions.V5_21_0
import org.neo4j.caniuse.Versions.V5_23_0
import org.neo4j.caniuse.Versions.V5_24_0
import org.neo4j.caniuse.Versions.V5_26_0
import org.neo4j.caniuse.Versions.V5_27_0
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
      major = 5,
      minor = 18,
      description = "the syntax was supported but ignored before 5.21.0",
  )
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
   * Whether `CYPHER 25` is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun explicitCypher25Selection(): Neo4jPredicate {
    return Neo4jPredicate {
          (it.version >= V2025_6_0) ||
              (it.deploymentType == Neo4jDeploymentType.AURA && it.version >= V5_27_0)
        }
        .and(explicitCypher5Selection())
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

  /**
   * Whether the given Cypher version is supported or not.
   *
   * @param version Cypher version to check for, e.g. "5" or "25"
   * @return [Neo4jPredicate]
   */
  fun version(version: String): Neo4jPredicate {
    return Neo4jPredicate { it.cypherVersions.contains(version) }
  }

  /**
   * Whether named constraints are supported
   *
   * @return [Neo4jPredicate]
   */
  fun namedConstraints(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V4_0_0 }
  }

  /**
   * Whether constraints with REQUIRE keywords are supported
   *
   * @return [Neo4jPredicate]
   */
  fun constraintsWithRequireKeyword(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V4_4_0 }
  }

  /**
   * Whether MATCH and MERGE with dynamic labels and relationship types can utilize indices when
   * matching/merging on property values.
   *
   * @return [Neo4jPredicate]
   */
  @PartiallyIntroducedIn(
      2025,
      11,
      0,
      "although the feature was first introduced in 2025.11.0, a critical bug affecting it " +
          "was fixed in 2026.01.4, so we consider the feature to be only reliably usable starting " +
          "from that version",
  )
  fun dynamicLabelsAndTypesCanLeveragePropertyIndices(): Neo4jPredicate {
    return Neo4jPredicate {
      (it.version >= V2026_01_4) ||
          (it.deploymentType == Neo4jDeploymentType.AURA && it.version >= V5_27_0)
    }
  }

  /**
   * Whether subqueries can access variables from the calling query using the variable scope clause.
   *
   * @return [Neo4jPredicate]
   */
  fun callSubqueryWithVariableScopeClause(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V5_23_0 }
  }

  /**
   * Whether `FINISH` clause is supported as a termination for queries.
   *
   * @return [Neo4jPredicate]
   */
  fun finishClause(): Neo4jPredicate {
    return Neo4jPredicate { it.version >= V5_19_0 }
  }
}
