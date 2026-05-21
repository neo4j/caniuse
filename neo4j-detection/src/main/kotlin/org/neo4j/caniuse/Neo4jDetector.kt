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
  /**
   * [detect] runs with the provided [Driver] to automatically detect the characteristics of the
   * Neo4j target.
   *
   * @return [Neo4j]
   */
  fun detect(driver: Driver): Neo4j {
    driver.session().use { session ->
      val params = mapOf("name" to "Neo4j Kernel")
      val result: org.neo4j.driver.Result =
          session.run(
              "CALL dbms.components() YIELD name, edition, versions WHERE name = \$name " +
                  "RETURN edition, versions[0] AS version LIMIT 1",
              params)
      val record = result.single()
      val rawVersion = record.get("version").asString()
      return Neo4j(
          Neo4jVersionParser.parse(rawVersion),
          parseEdition(record.get("edition").asString()),
          parseDeploymentType(rawVersion),
      )
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
