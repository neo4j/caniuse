package com.neo4j.data.importer;

class Neo4jVersionParser {

    public static Neo4jVersion parse(String version) {
        int major = -1;
        int minor = -1;
        int patch = -1;
        String buffer = "";
        for (char c : version.toCharArray()) {
            if (c != '.') {
                buffer += c;
                continue;
            }
            if (major == -1) {
                major = Integer.parseInt(buffer, 10);
            } else if (minor == -1) {
                minor = parseMinor(buffer);
            } else {
                throw invalidVersion(version);
            }
            buffer = "";
        }
        if (buffer.isEmpty()) {
            throw invalidVersion(version);
        }
        if (minor == -1) {
            minor = parseMinor(buffer);
        } else {
            patch = parsePatch(buffer);
        }

        if (major == -1 || minor == -1) {
            throw invalidVersion(version);
        }
        if (patch == -1) {
            return Neo4jVersion.of(major, minor);
        }
        return Neo4jVersion.of(major, minor, patch);
    }

    private static int parseMinor(String buffer) {
        return Integer.parseInt(buffer.replace("-aura", ""), 10);
    }

    private static int parsePatch(String buffer) {
        int patch;
        int end = buffer.indexOf('-');
        if (end == -1) {
            end = buffer.length();
        }
        patch = Integer.parseInt(buffer.substring(0, end), 10);
        return patch;
    }

    private static IllegalArgumentException invalidVersion(String version) {
        return new IllegalArgumentException(String.format("Invalid Neo4j kernel version: %s", version));
    }
}
