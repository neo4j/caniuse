package org.neo4j.caniuse

import org.neo4j.caniuse.Versions.V4_0_0
import org.neo4j.caniuse.Versions.V5_0_0

object Dbms {

  fun multiDatabase(): Neo4jPredicate {
    return Neo4jPredicate {
      it.edition === Neo4jEdition.ENTERPRISE &&
          it.version >= V4_0_0 &&
          it.deploymentType !== Neo4jDeploymentType.AURA
    }
  }

  fun compositeDatabases(): Neo4jPredicate {
    return Neo4jPredicate {
      it.edition === Neo4jEdition.ENTERPRISE &&
          it.version >= V5_0_0 &&
          it.deploymentType !== Neo4jDeploymentType.AURA
    }
  }
}
