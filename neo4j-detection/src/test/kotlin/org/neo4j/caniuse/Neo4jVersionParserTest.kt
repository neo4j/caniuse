/*
 * Copyright (c) "Neo4j"
 * Neo4j Sweden AB [https://neo4j.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    assertThat(parse("5.27.0-2025020")).isEqualTo(Neo4jVersion(2025, 2, 0))
    assertThat(parse("5.27.0-2026032")).isEqualTo(Neo4jVersion(2026, 3, 2))
    assertThat(parse("5.28.0-20250125")).isEqualTo(Neo4jVersion(2025, 1, 25))
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
