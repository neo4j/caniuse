package org.neo4j.caniuse

import java.util.regex.Pattern

internal object Neo4jVersionParser {

  // Matches versions with format: 5.27.0-2025020
  private val MIXED_CALVER_FORMAT = Pattern.compile("[^-]+-(\\d{4})(\\d{2})(\\d{1,})\$")

  fun parse(version: String): Neo4jVersion {

    // Check for special case with mixed [original-calver] format
    val mixCalVersion = parseRegex(version)
    if (mixCalVersion != null) {
      return mixCalVersion
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
  /**
   * Parse mixed CalVer format Expects format of the type: 5.27.0-2025020 which corresponds to
   * major=2025, minor=2, patch=0
   */
  fun parseRegex(version: String): Neo4jVersion? {
    val matcher = MIXED_CALVER_FORMAT.matcher(version)
    if (matcher.find()) {
      val major = matcher.group(1).toInt()
      val minor = matcher.group(2).toInt()
      val patch = matcher.group(3).toInt()
      return Neo4jVersion(major, minor, patch)
    }
    return null
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
