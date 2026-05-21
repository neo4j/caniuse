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

import java.io.Serializable
import kotlin.math.sign

/** Neo4j descriptor */
data class Neo4j(
    val version: Neo4jVersion,
    val edition: Neo4jEdition,
    val deploymentType: Neo4jDeploymentType,
) : Serializable {
  companion object {}
}

/** Neo4j version */
data class Neo4jVersion(
    val major: Int,
    val minor: Int,
    val patch: Int = Int.MAX_VALUE,
) : Comparable<Neo4jVersion>, Serializable {

  override fun compareTo(other: Neo4jVersion): Int {
    if (major != other.major) {
      return (major - other.major).sign
    }
    if (minor != other.minor) {
      return (minor - other.minor).sign
    }
    return (patch - other.patch).sign
  }

  companion object {
    val LATEST = Neo4jVersion(Int.MAX_VALUE, 0, 0)
  }
}

/** Neo4j edition */
enum class Neo4jEdition {
  COMMUNITY,
  ENTERPRISE
}

/** Neo4j deployment type */
enum class Neo4jDeploymentType {
  SELF_MANAGED,
  AURA
}
