package com.neo4j.data.importer;

import static com.neo4j.data.importer.Neo4jEdition.COMMUNITY;
import static com.neo4j.data.importer.Neo4jEdition.ENTERPRISE;
import static com.neo4j.data.importer.Neo4jEnvironment.ON_PREMISE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CypherTest {

    @CsvSource({"community,4,4,false", "enterprise,4,4,false", "community,5,0,true", "enterprise,5,0,true"})
    @ParameterizedTest
    void supports_calls_in_transactions(String rawEdition, String rawMajor, String rawMinor, String expectedResult) {
        Neo4jEdition edition = Neo4jEdition.valueOf(rawEdition.toUpperCase(Locale.ROOT));
        int major = Integer.parseInt(rawMajor, 10);
        int minor = Integer.parseInt(rawMinor, 10);
        boolean result = Boolean.parseBoolean(expectedResult);
        Neo4j neo4j = edition == COMMUNITY ? neo4jCE(major, minor) : neo4jEE(major, minor);

        assertThat(Cypher.callInTransactions().withNeo4j(neo4j)).isEqualTo(result);
    }

    @CsvSource({
        "community,4,4,false",
        "enterprise,4,4,false",
        "community,5,0,false",
        "enterprise,5,0,false",
        "community,5,7,true",
        "enterprise,5,7,true"
    })
    @ParameterizedTest
    void supports_calls_in_transactions_with_custom_error_policy(
            String rawEdition, String rawMajor, String rawMinor, String expectedResult) {
        Neo4jEdition edition = Neo4jEdition.valueOf(rawEdition.toUpperCase(Locale.ROOT));
        int major = Integer.parseInt(rawMajor, 10);
        int minor = Integer.parseInt(rawMinor, 10);
        boolean result = Boolean.parseBoolean(expectedResult);
        Neo4j neo4j = edition == COMMUNITY ? neo4jCE(major, minor) : neo4jEE(major, minor);

        assertThat(Cypher.callInTransactionsWithCustomErrorPolicy().withNeo4j(neo4j))
                .isEqualTo(result);
    }

    @CsvSource({
        "community,4,4,false",
        "enterprise,4,4,false",
        "community,5,0,false",
        "enterprise,5,0,false",
        "community,5,18,true",
        "enterprise,5,18,true"
    })
    @ParameterizedTest
    void supports_calls_in_transactions_with_composite_databases(
            String rawEdition, String rawMajor, String rawMinor, String expectedResult) {
        Neo4jEdition edition = Neo4jEdition.valueOf(rawEdition.toUpperCase(Locale.ROOT));
        int major = Integer.parseInt(rawMajor, 10);
        int minor = Integer.parseInt(rawMinor, 10);
        boolean result = Boolean.parseBoolean(expectedResult);
        Neo4j neo4j = edition == COMMUNITY ? neo4jCE(major, minor) : neo4jEE(major, minor);

        assertThat(Cypher.callInTransactionsWithCompositeDatabases().withNeo4j(neo4j))
                .isEqualTo(result);
    }

    @CsvSource({
        "community,4,4,false",
        "enterprise,4,4,false",
        "community,5,0,false",
        "enterprise,5,0,false",
        "community,5,21,true",
        "enterprise,5,21,true"
    })
    @ParameterizedTest
    void supports_concurrent_calls_in_transactions(
            String rawEdition, String rawMajor, String rawMinor, String expectedResult) {
        Neo4jEdition edition = Neo4jEdition.valueOf(rawEdition.toUpperCase(Locale.ROOT));
        int major = Integer.parseInt(rawMajor, 10);
        int minor = Integer.parseInt(rawMinor, 10);
        boolean result = Boolean.parseBoolean(expectedResult);
        Neo4j neo4j = edition == COMMUNITY ? neo4jCE(major, minor) : neo4jEE(major, minor);

        assertThat(Cypher.concurrentCallInTransactions().withNeo4j(neo4j)).isEqualTo(result);
    }

    private static Neo4j neo4jCE(int major, int minor) {
        return Neo4j.builder()
                .environment(ON_PREMISE)
                .edition(COMMUNITY)
                .version(Neo4jVersion.of(major, minor))
                .build();
    }

    private static Neo4j neo4jEE(int major, int minor) {
        return Neo4j.builder()
                .environment(ON_PREMISE)
                .edition(ENTERPRISE)
                .version(Neo4jVersion.of(major, minor))
                .build();
    }
}
