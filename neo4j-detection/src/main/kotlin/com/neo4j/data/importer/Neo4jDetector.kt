package com.neo4j.data.importer

import org.neo4j.driver.Driver

fun Neo4j.Companion.detectedWith(driver: Driver): Neo4j {
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

private fun parseDeploymentType(rawVersion: String): Neo4jEnvironment {
  if (rawVersion.endsWith("-aura")) {
    return Neo4jEnvironment.AURA
  }
  return Neo4jEnvironment.ON_PREMISE
}

private fun parseEdition(rawEdition: String): Neo4jEdition {
  val edition: String = rawEdition.lowercase()
  if (edition == "enterprise") {
    return Neo4jEdition.ENTERPRISE
  }
  return Neo4jEdition.COMMUNITY
}
