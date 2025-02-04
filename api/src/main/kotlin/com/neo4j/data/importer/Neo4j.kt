package com.neo4j.data.importer

import kotlin.math.sign

data class Neo4j(
    val version: Neo4jVersion,
    val edition: Neo4jEdition,
    val environment: Neo4jDeploymentType,
) {
  companion object {}
}

data class Neo4jVersion(
    val major: Int,
    val minor: Int,
    val patch: Int = Int.MAX_VALUE,
) : Comparable<Neo4jVersion> {

  override fun compareTo(other: Neo4jVersion): Int {
    if (major != other.major) {
      return (major - other.major).sign
    }
    if (minor != other.minor) {
      return (minor - other.minor).sign
    }
    return (patch - other.patch).sign
  }
}

enum class Neo4jEdition {
  COMMUNITY,
  ENTERPRISE
}

enum class Neo4jDeploymentType {
  SELF_MANAGED,
  AURA
}
