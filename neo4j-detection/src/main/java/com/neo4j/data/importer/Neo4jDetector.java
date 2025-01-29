package com.neo4j.data.importer;

import com.neo4j.data.importer.Neo4j.Builder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

public class Neo4jDetector {

    private Neo4jDetector() {}

    public static Neo4j detectWith(Driver driver) {
        try (Session session = driver.session()) {
            Map<String, Object> params = new HashMap<>(1);
            params.put("name", "Neo4j Kernel");
            Result result = session.run(
                    "CALL dbms.components() YIELD name, edition, versions WHERE name = $name "
                            + "RETURN edition, versions[0] AS version LIMIT 1",
                    params);
            Record record = result.single();
            String rawVersion = record.get("version").asString();
            Builder builder = Neo4j.builder();
            builder = builder.version(Neo4jVersionParser.parse(rawVersion));
            builder = builder.environment(parseDeploymentType(rawVersion));
            builder = builder.edition(parseEdition(record.get("edition").asString()));
            return builder.build();
        }
    }

    private static Neo4jEnvironment parseDeploymentType(String rawVersion) {
        if (rawVersion.endsWith("-aura")) {
            return Neo4jEnvironment.AURA;
        }
        return Neo4jEnvironment.ON_PREMISE;
    }

    private static Neo4jEdition parseEdition(String rawEdition) {
        String edition = rawEdition.toLowerCase(Locale.ROOT);
        if (edition.equals("enterprise")) {
            return Neo4jEdition.ENTERPRISE;
        }
        return Neo4jEdition.COMMUNITY;
    }
}
