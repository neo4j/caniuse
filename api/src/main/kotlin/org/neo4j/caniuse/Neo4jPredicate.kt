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

import java.util.function.Predicate

/**
 * Thin wrapper type over the built-in [Predicate]. This extends predicates to be tested against a
 * [Neo4j] descriptor. This is how feature detections are implemented.
 */
class Neo4jPredicate(private val predicate: Predicate<Neo4j>) {

  /**
   * Whether this feature detection passes against the provided [Neo4j] descriptor.
   *
   * @return [Boolean]
   */
  fun withNeo4j(neo4j: Neo4j): Boolean {
    return predicate.test(neo4j)
  }

  /**
   * Chains this feature detection with the provided one. Both must pass for the compound detection
   * in [withNeo4j] to return true.
   *
   * @return [Neo4jPredicate]
   */
  fun and(other: Neo4jPredicate): Neo4jPredicate {
    return Neo4jPredicate(predicate.and(other.predicate))
  }
}
