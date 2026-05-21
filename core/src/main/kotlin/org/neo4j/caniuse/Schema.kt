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
import org.neo4j.caniuse.Versions.V4_2_0
import org.neo4j.caniuse.Versions.V5_10_0
import org.neo4j.caniuse.Versions.V5_11_0
import org.neo4j.caniuse.Versions.V5_13_0
import org.neo4j.caniuse.Versions.V5_7_0
import org.neo4j.caniuse.Versions.V5_9_0

/** Main entry point for schema-related feature detections. */
object Schema {

  /**
   * Whether property type constraints are supported.
   *
   * @return [Neo4jPredicate]
   */
  fun propertyTypeConstraints(): Neo4jPredicate {
    return Neo4jPredicate { neo4j: Neo4j ->
      neo4j.edition === Neo4jEdition.ENTERPRISE && neo4j.version >= V5_9_0
    }
  }

  /**
   * Whether property list type constraints are supported.
   *
   * @return [Neo4jPredicate]
   */
  fun propertyListTypeConstraints(): Neo4jPredicate {
    return Neo4jPredicate { neo4j: Neo4j ->
      neo4j.edition === Neo4jEdition.ENTERPRISE && neo4j.version >= V5_10_0
    }
  }

  /**
   * Whether property union type constraints are supported.
   *
   * @return [Neo4jPredicate]
   */
  fun propertyUnionTypeConstraints(): Neo4jPredicate {
    return Neo4jPredicate { neo4j: Neo4j ->
      neo4j.edition === Neo4jEdition.ENTERPRISE && neo4j.version >= V5_11_0
    }
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

  /**
   * Whether node property uniqueness constraints are supported
   *
   * @return [Neo4jPredicate]
   */
  fun nodePropertyUniquenessConstraints(): Neo4jPredicate {
    return Neo4jPredicate { neo4j: Neo4j -> neo4j.version >= V4_0_0 }
  }

  /**
   * Whether node property existence constraints are supported
   *
   * @return [Neo4jPredicate]
   */
  fun nodePropertyExistenceConstraints(): Neo4jPredicate {
    return Neo4jPredicate { neo4j: Neo4j ->
      neo4j.edition == Neo4jEdition.ENTERPRISE && neo4j.version >= V4_0_0
    }
  }

  /**
   * Whether node key constraints are supported
   *
   * @return [Neo4jPredicate]
   */
  fun nodeKeyConstraints(): Neo4jPredicate {
    return Neo4jPredicate { neo4j: Neo4j ->
      neo4j.edition == Neo4jEdition.ENTERPRISE && neo4j.version >= V4_2_0
    }
  }

  /**
   * Whether relationship property existence constraints are supported
   *
   * @return [Neo4jPredicate]
   */
  fun relationshipPropertyExistenceConstraints(): Neo4jPredicate {
    return Neo4jPredicate { neo4j: Neo4j ->
      neo4j.edition == Neo4jEdition.ENTERPRISE && neo4j.version >= V4_0_0
    }
  }

  /**
   * Whether relationship property uniqueness constraints are supported
   *
   * @return [Neo4jPredicate]
   */
  fun relationshipPropertyUniquenessConstraints(): Neo4jPredicate {
    return Neo4jPredicate { neo4j: Neo4j -> neo4j.version >= V5_7_0 }
  }

  /**
   * Whether relationship key constraints are supported
   *
   * @return [Neo4jPredicate]
   */
  fun relationshipKeyConstraints(): Neo4jPredicate {
    return Neo4jPredicate { neo4j: Neo4j ->
      neo4j.edition == Neo4jEdition.ENTERPRISE && neo4j.version >= V5_7_0
    }
  }
}
