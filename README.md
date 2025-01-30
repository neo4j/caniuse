# Neo4j: Can I Use <X>?

Simple Neo4j Feature Detection Library for Neo4j clients.

## Prerequisites

### Build

 - JDK 11
 - Recent Maven (3+)

### Run

 - JRE 11+

## Quick Start

Add `com.neo4j.data.importer:neo4j-caniuse-core` to your project.

```java
import com.neo4j.data.importer.Cypher;
import com.neo4j.data.importer.Neo4j;
import com.neo4j.data.importer.Neo4jVersion;

import static com.neo4j.data.importer.CanIUse.canIUse;
import static com.neo4j.data.importer.Neo4jEdition.ENTERPRISE;
import static com.neo4j.data.importer.Neo4jEnvironment.ON_PREMISE;

public class Example {

    public static void main(String[] args) {
        Neo4j neo4j = Neo4j.builder()
                .edition(ENTERPRISE)
                .environment(ON_PREMISE)
                .version(Neo4jVersion.of(2025, 1))
                .build();
        if (canIUse(Cypher.concurrentCallInTransactions()).withNeo4j(neo4j)) {
            // run concurrent CALL IN TRANSACTIONS \o/
        }
    }
}
```

You can find other feature detections in `Cypher`, as well as `Dbms` and `Schema`.

## Neo4j detection

`neo4j-caniuse-core` only allows static definitions of your target Neo4j server's characteristics.

You can automatically retrieve these pieces of information by adding `com.neo4j.data.importer:neo4j-caniuse-detection` alongside `neo4j-caniuse-core` to your project.

The previous sample then becomes:

```java
import static com.neo4j.data.importer.CanIUse.canIUse;

import com.neo4j.data.importer.Cypher;
import com.neo4j.data.importer.Neo4j;
import com.neo4j.data.importer.Neo4jDetector;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Example {

    public static void main(String[] args) {
        try (Driver driver = GraphDatabase.driver("neo4j://localhost", AuthTokens.basic("neo4j", "letmein!"))) {
            Neo4j neo4j = Neo4jDetector.detectWith(driver);
            if (canIUse(Cypher.concurrentCallInTransactions()).withNeo4j(neo4j)) {
                // run concurrent CALL IN TRANSACTIONS \o/
            }
        }
    }
}
```

