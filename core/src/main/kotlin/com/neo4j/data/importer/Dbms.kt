package com.neo4j.data.importer

import com.neo4j.data.importer.Versions.V4_0_0
import com.neo4j.data.importer.Versions.V5_0_0

object Dbms {

  fun multiDatabase(): Neo4jPredicate {
    return Neo4jPredicate {
      it.edition === Neo4jEdition.ENTERPRISE &&
          it.version >= V4_0_0 &&
          it.environment !== Neo4jDeploymentType.AURA
    }
  }

  fun compositeDatabases(): Neo4jPredicate {
    return Neo4jPredicate {
      it.edition === Neo4jEdition.ENTERPRISE &&
          it.version >= V5_0_0 &&
          it.environment !== Neo4jDeploymentType.AURA
    }
  }
}
