# Neo4j: Can I Use <X>?

**This is an INTERNAL library designed to be used within official Neo4j products. No guarantees are made regarding its API stability and support.**

Simple Neo4j Feature Detection Library for Neo4j clients.

## Prerequisites

### Build

 - JDK 11
 - Recent Maven (3+)

### Run

 - JRE 11+

## Quick Start

Add `org.neo4j:caniuse-core` to your project.

```kotlin
import org.neo4j.caniuse.CanIUse.canIUse
import org.neo4j.caniuse.Cypher
import org.neo4j.caniuse.Neo4j
import org.neo4j.caniuse.Neo4jEdition.ENTERPRISE
import org.neo4j.caniuse.Neo4jDeploymentType.SELF_MANAGED
import org.neo4j.caniuse.Neo4jVersion

fun main(args: Array<String>) {
    val neo4j = Neo4j(Neo4jVersion(5, 26), ENTERPRISE, SELF_MANAGED)
    if (canIUse(Cypher.concurrentCallInTransactions()).withNeo4j(neo4j)) {
        // run concurrent CALL IN TRANSACTIONS \o/
    }
}

```

You can find other feature detections in `Cypher`, as well as `Dbms` and `Schema`.

## Neo4j detection

`org.neo4j:caniuse-core` only allows static definitions of your target Neo4j server's characteristics.

You can automatically retrieve these pieces of information by adding `org.neo4j:caniuse-neo4j-detection` alongside `caniuse-core` to your project.

The previous sample then becomes:

```kotlin
import org.neo4j.caniuse.CanIUse.canIUse
import org.neo4j.caniuse.Cypher
import org.neo4j.caniuse.Neo4jDetector
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase

fun main(args: Array<String>) {
    GraphDatabase.driver("neo4j://localhost", AuthTokens.basic("neo4j", "letmein!")).use { driver ->
        {
            val neo4j = Neo4j.detectedWith(driver)
            if (canIUse(Cypher.concurrentCallInTransactions()).withNeo4j(neo4j)) {
                // run concurrent CALL IN TRANSACTIONS \o/
            }
        }
    }
}
```

