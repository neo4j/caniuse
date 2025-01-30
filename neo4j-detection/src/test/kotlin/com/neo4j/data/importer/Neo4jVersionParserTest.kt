package com.neo4j.data.importer

import com.neo4j.data.importer.Neo4jVersionParser.parse
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class Neo4jVersionParserTest {
  @Test
  fun parses_kernel_version() {
    assertThat(parse("4.4")).isEqualTo(Neo4jVersion(4, 4))
    assertThat(parse("4.4-aura")).isEqualTo(Neo4jVersion(4, 4))
    assertThat(parse("4.4.13")).isEqualTo(Neo4jVersion(4, 4, 13))
    assertThat(parse("2025.01")).isEqualTo(Neo4jVersion(2025, 1))
    assertThat(parse("2025.01.0")).isEqualTo(Neo4jVersion(2025, 1, 0))
    assertThat(parse("2025.01-aura")).isEqualTo(Neo4jVersion(2025, 1))
    assertThat(parse("2025.01.0-21379")).isEqualTo(Neo4jVersion(2025, 1, 0))
  }

  @Test
  fun rejects_invalid_kernel_version() {
    assertThatThrownBy { parse("") }
        .isInstanceOf(IllegalArgumentException::class.java)
        .hasMessageStartingWith("Invalid Neo4j kernel version: ")
    assertThatThrownBy { parse("5") }
        .isInstanceOf(IllegalArgumentException::class.java)
        .hasMessageStartingWith("Invalid Neo4j kernel version: ")
    assertThatThrownBy { parse("5.") }
        .isInstanceOf(IllegalArgumentException::class.java)
        .hasMessageStartingWith("Invalid Neo4j kernel version: ")
    assertThatThrownBy { parse("2025.1.") }
        .isInstanceOf(IllegalArgumentException::class.java)
        .hasMessageStartingWith("Invalid Neo4j kernel version: ")
    assertThatThrownBy { parse("5.5.3.1") }
        .isInstanceOf(IllegalArgumentException::class.java)
        .hasMessageStartingWith("Invalid Neo4j kernel version: ")
    assertThatThrownBy { parse("..") }.isInstanceOf(NumberFormatException::class.java)
    assertThatThrownBy { parse("5..3") }.isInstanceOf(NumberFormatException::class.java)
    assertThatThrownBy { parse(".2025.6") }.isInstanceOf(NumberFormatException::class.java)
  }
}
