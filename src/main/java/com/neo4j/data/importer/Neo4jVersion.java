package com.neo4j.data.importer;

import java.util.Objects;

public class Neo4jVersion implements Comparable<Neo4jVersion> {

    static final Neo4jVersion V4_0_0 = new Neo4jVersion(4, 0, 0);
    static final Neo4jVersion V4_1_3 = new Neo4jVersion(4, 1, 3);
    static final Neo4jVersion V4_3_0 = new Neo4jVersion(4, 3, 0);
    static final Neo4jVersion V4_4_0 = new Neo4jVersion(4, 4, 0);
    static final Neo4jVersion V5_0_0 = new Neo4jVersion(5, 0, 0);
    static final Neo4jVersion V5_7_0 = new Neo4jVersion(5, 7, 0);
    static final Neo4jVersion V5_9_0 = new Neo4jVersion(5, 9, 0);
    static final Neo4jVersion V5_10_0 = new Neo4jVersion(5, 10, 0);
    static final Neo4jVersion V5_11_0 = new Neo4jVersion(5, 11, 0);
    static final Neo4jVersion V5_13_0 = new Neo4jVersion(5, 13, 0);
    static final Neo4jVersion V5_18_0 = new Neo4jVersion(5, 18, 0);
    static final Neo4jVersion V5_21_0 = new Neo4jVersion(5, 21, 0);
    static final Neo4jVersion V5_24_0 = new Neo4jVersion(5, 24, 0);
    static final Neo4jVersion V5_26_0 = new Neo4jVersion(5, 26, 0);

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
