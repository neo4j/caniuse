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

import org.neo4j.caniuse.Versions.V4_0_0
import org.neo4j.caniuse.Versions.V5_0_0
import org.neo4j.caniuse.Versions.V5_13_0

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
    return Neo4jPredicate { it.edition === Neo4jEdition.ENTERPRISE && it.version >= V5_13_0 }
  }
}
