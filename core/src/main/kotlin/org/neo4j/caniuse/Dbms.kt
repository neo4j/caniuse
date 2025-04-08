package org.neo4j.caniuse

import org.neo4j.caniuse.Versions.V4_0_0
import org.neo4j.caniuse.Versions.V5_0_0
import org.neo4j.caniuse.Versions.V5_23_0

/** Main entry point for DBMS-related feature/capability detections. */
object Dbms {

  /**
   * Whether multi-tenancy (multi-database) is supported.
   *
   * @return [Neo4jPredicate]
   */
  fun multiDatabase(): Neo4jPredicate {
    return Neo4jPredicate {
      it.edition === Neo4jEdition.ENTERPRISE &&
          it.version >= V4_0_0 &&
          it.deploymentType !== Neo4jDeploymentType.AURA
    }
  }

  /**
   * Whether composite databases are supported.
   *
   * @return [Neo4jPredicate]
   */
  fun compositeDatabases(): Neo4jPredicate {
    return Neo4jPredicate {
      it.edition === Neo4jEdition.ENTERPRISE &&
          it.version >= V5_0_0 &&
          it.deploymentType !== Neo4jDeploymentType.AURA
    }
  }

  /**
   * Whether change data capture is supported
   *
   * @return [Neo4jPredicate]
   */
  fun changeDataCapture(): Neo4jPredicate {
    return Neo4jPredicate { it.edition === Neo4jEdition.ENTERPRISE && it.version >= V5_23_0 }
  }
}
