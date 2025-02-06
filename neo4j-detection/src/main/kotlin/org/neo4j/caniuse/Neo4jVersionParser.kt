package org.neo4j.caniuse

internal object Neo4jVersionParser {

  fun parse(version: String): Neo4jVersion {
    var major = -1
    var minor = -1
    var patch = -1
    var buffer = ""
    for (c in version.toCharArray()) {
      if (c != '.') {
        buffer += c
        continue
      }
      if (major == -1) {
        major = buffer.toInt(10)
      } else if (minor == -1) {
        minor = parseMinor(buffer)
      } else {
        throw invalidVersion(version)
      }
      buffer = ""
    }
    if (buffer.isEmpty()) {
      throw invalidVersion(version)
    }
    if (minor == -1) {
      minor = parseMinor(buffer)
    } else {
      patch = parsePatch(buffer)
    }

    if (major == -1 || minor == -1) {
      throw invalidVersion(version)
    }
    if (patch == -1) {
      return Neo4jVersion(major, minor)
    }
    return Neo4jVersion(major, minor, patch)
  }

  private fun parseMinor(buffer: String): Int {
    return buffer.replace("-aura", "").toInt(10)
  }

  private fun parsePatch(buffer: String): Int {
    val patch: Int
    var end: Int = buffer.indexOf('-')
    if (end == -1) {
      end = buffer.length
    }
    patch = buffer.substring(0, end).toInt(10)
    return patch
  }

  private fun invalidVersion(version: String): IllegalArgumentException {
    return IllegalArgumentException(String.format("Invalid Neo4j kernel version: %s", version))
  }
}
