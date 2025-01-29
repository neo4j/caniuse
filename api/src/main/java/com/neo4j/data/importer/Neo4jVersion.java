package com.neo4j.data.importer;

import java.util.Objects;

public class Neo4jVersion implements Comparable<Neo4jVersion> {

    private final int major;
    private final int minor;
    private final int patch;

    private Neo4jVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static Neo4jVersion of(int major, int minor) {
        return of(major, minor, Integer.MAX_VALUE);
    }

    public static Neo4jVersion of(int major, int minor, int patch) {
        return new Neo4jVersion(major, minor, patch);
    }

    public boolean greaterThanOrEqual(Neo4jVersion other) {
        return this.compareTo(other) >= 0;
    }

    @Override
    public int compareTo(Neo4jVersion other) {
        if (major != other.major) {
            return signum(major - other.major);
        }
        if (minor != other.minor) {
            return signum(minor - other.minor);
        }
        return signum(patch - other.patch);
    }

    // visible for testing
    int major() {
        return major;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Neo4jVersion)) {
            return false;
        }
        Neo4jVersion other = (Neo4jVersion) o;
        return major == other.major && minor == other.minor && patch == other.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public String toString() {
        if (patch == Integer.MAX_VALUE) {
            return String.format("%d.%d", major, minor);
        }
        return String.format("%d.%d.%d", major, minor, patch);
    }

    private static int signum(int result) {
        return (int) Math.signum(result);
    }
}
