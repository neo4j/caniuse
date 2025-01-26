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
import com.neo4j.data.importer.Neo4j;
import com.neo4j.data.importer.Neo4jVersion;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import static com.neo4j.data.importer.Neo4jEnvironment.ON_PREMISE;
import static com.neo4j.data.importer.Neo4jEdition.ENTERPRISE;
import static com.neo4j.data.importer.Neo4jSupport.detectedNeo4jSupports;
import static com.neo4j.data.importer.Neo4jSupport.neo4jSupports;

class Example {
    public static void main(String[] args) {
        // static variant
        Neo4j neo4j = Neo4j.builder()
                .edition(ENTERPRISE)
                .environment(ON_PREMISE)
                .version(Neo4jVersion.of(2025, 1))
                .build();

        if (neo4jSupports(neo4j).cypher().concurrentCallInTransactions()) {
            // run concurrent CALL IN TRANSACTIONS \o/
        }

        // dynamic variant
        try (Driver driver = GraphDatabase.driver("neo4j://localhost", AuthTokens.basic("neo4j", "letmein!"))) {
            if (detectedNeo4jSupports(driver).cypher().concurrentCallInTransactions()) {
                // run concurrent CALL IN TRANSACTIONS \o/
            }
        }
    }

}
```

