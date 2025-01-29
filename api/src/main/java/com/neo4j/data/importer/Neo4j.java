package com.neo4j.data.importer;

import java.util.Objects;

public class Neo4j {

    private final Neo4jVersion version;
    private final Neo4jEdition edition;
    private final Neo4jEnvironment environment;

    private Neo4j(Neo4jVersion version, Neo4jEdition edition, Neo4jEnvironment environment) {
        this.version = version;
        this.edition = edition;
        this.environment = environment;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Neo4j neo4j) {
        return new Builder(neo4j);
    }

    public Neo4jVersion version() {
        return version;
    }

    public Neo4jEdition edition() {
        return edition;
    }

    public Neo4jEnvironment environment() {
        return environment;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Neo4j)) return false;
        Neo4j neo4j = (Neo4j) o;
        return Objects.equals(version, neo4j.version) && edition == neo4j.edition && environment == neo4j.environment;
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, edition, environment);
    }

    @Override
    public String toString() {
        return "Neo4j{" + "version=" + version + ", edition=" + edition + ", deploymentType=" + environment + '}';
    }

    public static class Builder {
        private Neo4jVersion version;
        private Neo4jEdition edition;
        private Neo4jEnvironment environment;

        Builder() {}

        Builder(Neo4j neo4j) {
            this.version = neo4j.version();
            this.edition = neo4j.edition();
            this.environment = neo4j.environment();
        }

        public Builder version(Neo4jVersion version) {
            this.version = version;
            return this;
        }

        public Builder edition(Neo4jEdition edition) {
            this.edition = edition;
            return this;
        }

        public Builder environment(Neo4jEnvironment environment) {
            this.environment = environment;
            return this;
        }

        public Neo4j build() {
            if (version == null) {
                throw new IllegalStateException("version cannot be null");
            }
            if (edition == null) {
                throw new IllegalStateException("edition cannot be null");
            }
            if (environment == null) {
                throw new IllegalStateException("deploymentType cannot be null");
            }
            return new Neo4j(version, edition, environment);
        }
    }
}
