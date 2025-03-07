package org.neo4j.caniuse

import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.params.aggregator.ArgumentsAccessor
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException
import org.junit.jupiter.params.aggregator.ArgumentsAggregator

class Neo4jAggregator : ArgumentsAggregator {

    @Throws(ArgumentsAggregationException::class)
    override fun aggregateArguments(accessor: ArgumentsAccessor, context: ParameterContext): Any {
        val edition: Neo4jEdition = Neo4jEdition.valueOf(accessor.getString(1).uppercase())
        val version: Neo4jVersion = getVersion(accessor)
        return if (edition == Neo4jEdition.COMMUNITY) {
            neo4jCE(version)
        } else {
            neo4jEE(version)
        }
    }

    companion object {
        private fun getVersion(accessor: ArgumentsAccessor): Neo4jVersion {
            val major = accessor.getString(2).toInt(10)
            val minor = accessor.getString(3).toInt(10)
            return if (accessor.size() == 5) {
                val patch = accessor.getString(4).toInt(10)
                Neo4jVersion(major, minor, patch)
            } else {
                Neo4jVersion(major, minor)
            }
        }

        private fun neo4jCE(version: Neo4jVersion): Neo4j {
            return Neo4j(version, Neo4jEdition.COMMUNITY, Neo4jDeploymentType.SELF_MANAGED)
        }

        private fun neo4jEE(version: Neo4jVersion): Neo4j {
            return Neo4j(version, Neo4jEdition.ENTERPRISE, Neo4jDeploymentType.SELF_MANAGED)
        }
    }
}
