# Neo4j | Can I Use?

Simple Neo4j Feature Detection Library for Neo4j clients.

## Prerequisites

### Build

 - JDK 8+
 - Recent Maven (3+)

### Run

 - JRE 8+

## Quick Start

```java
import static com.neo4j.data.importer.CanIUse.canIUse;
import static com.neo4j.data.importer.Neo4jEdition.ENTERPRISE;
import static com.neo4j.data.importer.Neo4jEnvironment.ON_PREMISE;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Example {

    public static void main(String[] args) {
        // static variant
        Neo4j neo4j = Neo4j.builder()
                .edition(ENTERPRISE)
                .environment(ON_PREMISE)
                .version(Neo4jVersion.of(2025, 1))
                .build();
        if (canIUse(Cypher.concurrentCallInTransactions()).withNeo4j(neo4j)) {
            // run concurrent CALL IN TRANSACTIONS \o/
        }

        // dynamic variant
        try (Driver driver = GraphDatabase.driver("neo4j://localhost", AuthTokens.basic("neo4j", "letmein!"))) {
            if (canIUse(Cypher.concurrentCallInTransactions()).withDetectedNeo4j(driver)) {
                // run concurrent CALL IN TRANSACTIONS \o/
            }
        }
    }
}


```

