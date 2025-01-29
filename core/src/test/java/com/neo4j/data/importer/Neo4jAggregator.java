package com.neo4j.data.importer;

import static com.neo4j.data.importer.Neo4jEdition.COMMUNITY;
import static com.neo4j.data.importer.Neo4jEdition.ENTERPRISE;
import static com.neo4j.data.importer.Neo4jEnvironment.ON_PREMISE;

import java.util.Locale;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

public class Neo4jAggregator implements ArgumentsAggregator {

    @Override
    public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context)
            throws ArgumentsAggregationException {
        Neo4jEdition edition = Neo4jEdition.valueOf(accessor.getString(1).toUpperCase(Locale.ROOT));
        Neo4jVersion version = getVersion(accessor);
        if (edition == COMMUNITY) {
            return neo4jCE(version);
        }
        return neo4jEE(version);
    }

    private static Neo4jVersion getVersion(ArgumentsAccessor accessor) {
        int major = Integer.parseInt(accessor.getString(2), 10);
        int minor = Integer.parseInt(accessor.getString(3), 10);
        Neo4jVersion version;
        if (accessor.size() == 5) {
            int patch = Integer.parseInt(accessor.getString(4), 10);
            version = Neo4jVersion.of(major, minor, patch);
        } else {
            version = Neo4jVersion.of(major, minor);
        }
        return version;
    }

    private static Neo4j neo4jCE(Neo4jVersion version) {
        return Neo4j.builder()
                .environment(ON_PREMISE)
                .edition(COMMUNITY)
                .version(version)
                .build();
    }

    private static Neo4j neo4jEE(Neo4jVersion version) {
        return Neo4j.builder()
                .environment(ON_PREMISE)
                .edition(ENTERPRISE)
                .version(version)
                .build();
    }
}
