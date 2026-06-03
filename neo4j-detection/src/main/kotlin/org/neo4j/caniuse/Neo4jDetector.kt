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

import org.neo4j.driver.Driver
import org.neo4j.driver.Value

/**
 * [detectedWith] runs with the provided [Driver] to automatically detect the characteristics of the
 * Neo4j target. Deprecated since 1.1.0.
 *
 * @return [Neo4j]
 */
@Deprecated(
    message = "please use Neo4jDetector.detect(driver: Driver) instead",
    replaceWith = ReplaceWith("Neo4jDetector.detect(driver)"))
fun Neo4j.Companion.detectedWith(driver: Driver): Neo4j {
  return Neo4jDetector.detect(driver)
}

object Neo4jDetector {

  private const val CYPHER = "Cypher"

  private const val NEO4J_KERNEL = "Neo4j Kernel"

  /**
   * [detect] runs with the provided [Driver] to automatically detect the characteristics of the
   * Neo4j target.
   *
   * @return [Neo4j]
   */
  fun detect(driver: Driver): Neo4j {
    driver.session().use { session ->
      val params = mapOf("names" to listOf(CYPHER, NEO4J_KERNEL))
      val result: org.neo4j.driver.Result =
          session.run(
              "CALL dbms.components() YIELD name, edition, versions WHERE name IN \$names RETURN name, edition, versions",
              params)
      val records = result.list()

      // check error states
      when (records.size) {
        0 -> throw IllegalStateException("Could not find Neo4j Kernel or Cypher")
        1 ->
            records[0].get("name").asString().takeIf { it == NEO4J_KERNEL }
                ?: throw IllegalStateException("Invalid dbms.components() response from server")
        in 3..Int.MAX_VALUE ->
            throw IllegalStateException("Invalid dbms.components() response from server")
      }

      var rawVersion = ""
      var rawEdition = ""
      var rawCyphers: Set<String> = setOf()

      records.forEach { record ->
        when (record.get("name").asString()) {
          NEO4J_KERNEL -> {
            rawVersion =
                record.get("versions").asList(Value::asString).singleOrNull()
                    ?: throw IllegalStateException("Could not find version")
            rawEdition = record.get("edition").asString()
          }

          CYPHER -> {
            rawCyphers = record.get("versions").asList(Value::asString).toSet()
          }
        }
      }

      val version = Neo4jVersionParser.parse(rawVersion)

      if (rawCyphers.isEmpty()) {
        // TODO: Can we use org.neo4j.caniuse.Cypher#explicitCypherSelection
        if (version >= Neo4jVersion(5, 21, 0)) {
          rawCyphers = setOf("5")
        }
      }

      return Neo4j(version, parseEdition(rawEdition), parseDeploymentType(rawVersion), rawCyphers)
    }
  }

  private fun parseDeploymentType(rawVersion: String): Neo4jDeploymentType {
    if (rawVersion.endsWith("-aura")) {
      return Neo4jDeploymentType.AURA
    }
    return Neo4jDeploymentType.SELF_MANAGED
  }

  private fun parseEdition(rawEdition: String): Neo4jEdition {
    val edition: String = rawEdition.lowercase()
    if (edition == "enterprise") {
      return Neo4jEdition.ENTERPRISE
    }
    return Neo4jEdition.COMMUNITY
  }
}
