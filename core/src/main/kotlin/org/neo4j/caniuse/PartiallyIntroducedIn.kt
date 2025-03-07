package org.neo4j.caniuse

/**
 * This is an internal utility to indicate when a feature has actually been introduced, even though it was not fully
 * stable at that time. In these cases, the documented version of such features is typically set to a later release.
 */
@Target(AnnotationTarget.FUNCTION)
internal annotation class PartiallyIntroducedIn(
    val major: Int,
    val minor: Int,
    val patch: Int = 0,
    val edition: String = "",
    val deploymentType: String = "",
    val description: String = ""
)

internal fun asNeo4jPredicate(annotation: PartiallyIntroducedIn): Neo4jPredicate {
    return Neo4jPredicate { neo4j: Neo4j ->
        var result = neo4j.version >= Neo4jVersion(annotation.major, annotation.minor, annotation.patch)
        if (annotation.edition != "") {
            result = result && neo4j.edition == Neo4jEdition.valueOf(annotation.edition)
        }
        if (annotation.deploymentType != "") {
            result = result && neo4j.deploymentType == Neo4jDeploymentType.valueOf(annotation.deploymentType)
        }
        result
    }
}
