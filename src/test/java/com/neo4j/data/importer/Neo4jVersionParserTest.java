package com.neo4j.data.importer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class Neo4jVersionParserTest {

    @Test
    void parses_kernel_version() {
        assertThat(Neo4jVersionParser.parse("4.4")).isEqualTo(Neo4jVersion.of(4, 4));
        assertThat(Neo4jVersionParser.parse("4.4-aura")).isEqualTo(Neo4jVersion.of(4, 4));
        assertThat(Neo4jVersionParser.parse("4.4.13")).isEqualTo(Neo4jVersion.of(4, 4, 13));
        assertThat(Neo4jVersionParser.parse("2025.01")).isEqualTo(Neo4jVersion.of(2025, 1));
        assertThat(Neo4jVersionParser.parse("2025.01.0")).isEqualTo(Neo4jVersion.of(2025, 1, 0));
        assertThat(Neo4jVersionParser.parse("2025.01-aura")).isEqualTo(Neo4jVersion.of(2025, 1));
        assertThat(Neo4jVersionParser.parse("2025.01.0-21379")).isEqualTo(Neo4jVersion.of(2025, 1, 0));
    }

    @Test
    public void rejects_invalid_kernel_version() {
        assertThatThrownBy(() -> Neo4jVersionParser.parse(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Invalid Neo4j kernel version: ");
        assertThatThrownBy(() -> Neo4jVersionParser.parse("5"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Invalid Neo4j kernel version: ");
        assertThatThrownBy(() -> Neo4jVersionParser.parse("5."))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Invalid Neo4j kernel version: ");
        assertThatThrownBy(() -> Neo4jVersionParser.parse("2025.1."))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Invalid Neo4j kernel version: ");
        assertThatThrownBy(() -> Neo4jVersionParser.parse("5.5.3.1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Invalid Neo4j kernel version: ");
        assertThatThrownBy(() -> Neo4jVersionParser.parse("..")).isInstanceOf(NumberFormatException.class);
        assertThatThrownBy(() -> Neo4jVersionParser.parse("5..3")).isInstanceOf(NumberFormatException.class);
        assertThatThrownBy(() -> Neo4jVersionParser.parse(".2025.6")).isInstanceOf(NumberFormatException.class);
    }
}
