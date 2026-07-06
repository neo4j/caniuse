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

import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.params.aggregator.ArgumentsAccessor
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException
import org.junit.jupiter.params.aggregator.ArgumentsAggregator

class Neo4jAggregator : ArgumentsAggregator {

  @Throws(ArgumentsAggregationException::class)
  override fun aggregateArguments(accessor: ArgumentsAccessor, context: ParameterContext): Any {
    var deploymentType = Neo4jDeploymentType.SELF_MANAGED
    val edition: Neo4jEdition =
        when (accessor.getString(1)?.uppercase()) {
          "AURA" -> {
            deploymentType = Neo4jDeploymentType.AURA
            Neo4jEdition.ENTERPRISE
          }
          "ENTERPRISE" -> Neo4jEdition.ENTERPRISE
          "COMMUNITY" -> Neo4jEdition.COMMUNITY
          else ->
              throw ArgumentsAggregationException("Unknown Neo4j edition: ${accessor.getString(1)}")
        }
    val version: Neo4jVersion = getVersion(accessor)
    return if (edition == Neo4jEdition.COMMUNITY) {
      neo4jCE(version)
    } else {
      neo4jEE(version, deploymentType)
    }
  }

  companion object {
    private fun getVersion(accessor: ArgumentsAccessor): Neo4jVersion {
      val major = accessor.getString(2)?.toInt(10)
      val minor = accessor.getString(3)?.toInt(10)
      return if (accessor.size() == 5) {
        val patch = accessor.getString(4)?.toInt(10)
        Neo4jVersion(requireNotNull(major), requireNotNull(minor), requireNotNull(patch))
      } else {
        Neo4jVersion(requireNotNull(major), requireNotNull(minor))
      }
    }

    private fun neo4jCE(version: Neo4jVersion): Neo4j {
      return Neo4j(version, Neo4jEdition.COMMUNITY, Neo4jDeploymentType.SELF_MANAGED)
    }

    private fun neo4jEE(
        version: Neo4jVersion,
        deploymentType: Neo4jDeploymentType = Neo4jDeploymentType.SELF_MANAGED,
    ): Neo4j {
      return Neo4j(version, Neo4jEdition.ENTERPRISE, deploymentType)
    }
  }
}
