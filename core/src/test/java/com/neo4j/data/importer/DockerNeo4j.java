package com.neo4j.data.importer;

public class DockerNeo4j {

    public static String image() {
        return String.format("neo4j:%s", tag());
    }

    private static String tag() {
        String version = version();
        if (enterprise()) {
            return String.format("%s-enterprise", version);
        }
        return version;
    }

    static String version() {
        String version = System.getenv("NEO4J_VERSION");
        if (version == null) {
            return "4.4";
        }
        return version;
    }

    static boolean enterprise() {
        return Boolean.parseBoolean(System.getenv("ENTERPRISE"));
    }
}
