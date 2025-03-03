package org.neo4j.caniuse

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.neo4j.caniuse.Neo4jVersionParser.parse

internal class Neo4jVersionParserTest {
  @Test
  fun parses_neo4j_version() {
    assertThat(parse("4.4")).isEqualTo(Neo4jVersion(4, 4))
    assertThat(parse("4.4-aura")).isEqualTo(Neo4jVersion(4, 4))
    assertThat(parse("4.4.13")).isEqualTo(Neo4jVersion(4, 4, 13))
    assertThat(parse("2025.01")).isEqualTo(Neo4jVersion(2025, 1))
    assertThat(parse("2025.01.0")).isEqualTo(Neo4jVersion(2025, 1, 0))
    assertThat(parse("2025.01-aura")).isEqualTo(Neo4jVersion(2025, 1))
    assertThat(parse("2025.01.0-21379")).isEqualTo(Neo4jVersion(2025, 1, 0))
    assertThat(parse("5.27.0-2025020")).isEqualTo(Neo4jVersion(2025, 2))
    assertThat(parse("5.27.0-2026030")).isEqualTo(Neo4jVersion(2026, 3))
  }

  @Test
  fun assumes_latest_when_failing_to_parse_neo4j_version() {
    assertThat(parse("")).isEqualTo(Neo4jVersion.LATEST)
    assertThat(parse("  ")).isEqualTo(Neo4jVersion.LATEST)
    assertThat(parse("foobar")).isEqualTo(Neo4jVersion.LATEST)
    assertThat(parse("5")).isEqualTo(Neo4jVersion.LATEST)
    assertThat(parse("5.")).isEqualTo(Neo4jVersion.LATEST)
    assertThat(parse("2025.1.")).isEqualTo(Neo4jVersion.LATEST)
    assertThat(parse("5.5.3.1")).isEqualTo(Neo4jVersion.LATEST)
    assertThat(parse("..")).isEqualTo(Neo4jVersion.LATEST)
    assertThat(parse("5..3")).isEqualTo(Neo4jVersion.LATEST)
    assertThat(parse(".2025.6")).isEqualTo(Neo4jVersion.LATEST)
    assertThat(parse("  5 .26.2    ")).isEqualTo(Neo4jVersion.LATEST)
    assertThat(parse("  5 . 26 .2    ")).isEqualTo(Neo4jVersion.LATEST)
  }
}
