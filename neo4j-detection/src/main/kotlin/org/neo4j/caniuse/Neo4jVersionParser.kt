package org.neo4j.caniuse

internal object Neo4jVersionParser {

  fun parse(version: String): Neo4jVersion {
    // Check for special format with calver hyphen
    if (version.contains('-')) {
      val afterHyphen = version.substringAfter('-')
      if (afterHyphen.length >= 6) { // Expecting format like "2025020"
        try {
          val majorYear = afterHyphen.substring(0, 4).toInt()
          val minorMonth = afterHyphen.substring(4, 6).toInt()
          val patch = afterHyphen.substring(6).toInt()
          return Neo4jVersion(majorYear, minorMonth, patch)
        } catch (_: NumberFormatException) {
          return Neo4jVersion.LATEST
        }
      }
    }

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
        try {
          major = buffer.toInt(10)
        } catch (_: NumberFormatException) {
          return Neo4jVersion.LATEST
        }
      } else if (minor == -1) {
        try {
          minor = parseMinor(buffer)
        } catch (_: NumberFormatException) {
          return Neo4jVersion.LATEST
        }
      } else {
        return Neo4jVersion.LATEST
      }
      buffer = ""
    }
    if (buffer.isEmpty()) {
      return Neo4jVersion.LATEST
    }
    if (minor == -1) {
      try {
        minor = parseMinor(buffer)
      } catch (_: NumberFormatException) {
        return Neo4jVersion.LATEST
      }
    } else {
      try {
        patch = parsePatch(buffer)
      } catch (_: NumberFormatException) {
        return Neo4jVersion.LATEST
      }
    }

    if (major == -1 || minor == -1) {
      return Neo4jVersion.LATEST
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
}
