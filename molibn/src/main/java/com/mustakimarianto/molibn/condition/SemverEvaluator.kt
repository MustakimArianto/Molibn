package com.mustakimarianto.molibn.condition

import android.util.Log
import java.util.Locale

/**
 * Utility object to evaluate semantic version conditions against a given app version.
 *
 * Internal to the library — not part of the public API.
 */
internal object SemverEvaluator {
    private const val TAG = "SemverEvaluator"

    /**
     * Evaluate whether the given `appVersion` satisfies a semantic version condition.
     *
     * Supported condition formats:
     * - ">=": greater than or equal (e.g., ">=1.2.0")
     * - ">" : strictly greater than (e.g., ">2.0.0")
     * - "<=": less than or equal (e.g., "<=3.0.0")
     * - "<" : strictly less than (e.g., "<1.5.0")
     * - "range": hyphen-based range inclusive (e.g., "1.0.0-2.0.0")
     * - "exact match": exact string match (e.g., "2.0.0" or "2.0-beta")
     *
     * Behavior:
     * - Uses [compare] to handle numeric and pre-release ordering.
     * - If condition parsing fails, it logs a warning and returns `false`.
     *
     * Examples:
     * ```
     * evaluate("2.1.0", ">=2.0.0") // true
     * evaluate("1.0.0-alpha", "<1.0.0") // true
     * evaluate("1.5.0", "1.0.0-2.0.0") // true
     * evaluate("2.0.0-beta", "2.0.0") // false (pre-release < release)
     * ```
     *
     * @param appVersion the current version string (e.g., "2.1.0", "1.0.0-beta")
     * @param condition a condition string (e.g., ">=1.0.0", "<2.0.0", "1.0.0-2.0.0")
     * @return true if the condition passes, false otherwise
     */
    fun evaluate(appVersion: String, condition: String): Boolean {
        val conditionTrimmed = condition.trim().lowercase(Locale.US)

        return try {
            when {
                conditionTrimmed.startsWith(">=") -> {
                    val target = conditionTrimmed.removePrefix(">=").trim()
                    compare(appVersion, target) >= 0
                }

                conditionTrimmed.startsWith(">") -> {
                    val target = conditionTrimmed.removePrefix(">").trim()
                    compare(appVersion, target) > 0
                }

                conditionTrimmed.startsWith("<=") -> {
                    val target = conditionTrimmed.removePrefix("<=").trim()
                    compare(appVersion, target) <= 0
                }

                conditionTrimmed.startsWith("<") -> {
                    val target = conditionTrimmed.removePrefix("<").trim()
                    compare(appVersion, target) < 0
                }

                "-" in conditionTrimmed -> {
                    val parts = conditionTrimmed.split("-", limit = 2).map { it.trim() }
                    if (parts.size == 2) {
                        val (min, max) = parts
                        compare(appVersion, min) >= 0 && compare(appVersion, max) <= 0
                    } else {
                        false
                    }
                }

                else -> {
                    appVersion == conditionTrimmed
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to evaluate semver condition: $condition", e)
            false
        }
    }

    /**
     * Compare two semantic version strings with optional pre-release tags.
     *
     * Supported formats:
     * - Core version: "1.0.0", "2.1", "3"
     * - Pre-release:  "1.0.0-alpha", "1.0.0-beta", "1.0.0-rc"
     *
     * Comparison rules:
     * 1. Split version into two parts: main (e.g., "1.0.0") and pre-release (e.g., "alpha").
     * 2. Compare main versions numerically by each dot-separated part:
     *    - Example: "2.1.0" > "2.0.9"
     *    - Missing parts are treated as zero → "1.0" == "1.0.0"
     * 3. If main parts are equal, handle pre-release tags:
     *    - A release (no pre-release) is considered greater than any pre-release.
     *      Example: "1.0.0" > "1.0.0-beta"
     *    - If both have pre-release tags, compare them lexicographically.
     *      Example: "alpha" < "beta" < "rc"
     *
     * NOTE: This default uses lexicographic comparison for pre-release tags.
     * If you want a custom precedence (e.g., alpha < beta < rc < snapshot < release),
     * you should implement a custom ranking instead of raw string compare.
     *
     * @return negative if v1 < v2, zero if v1 == v2, positive if v1 > v2
     */
    private fun compare(v1: String, v2: String): Int {
        val main1 = v1.split("-", limit = 2)
        val main2 = v2.split("-", limit = 2)

        val parts1 = main1[0].split(".")
        val parts2 = main2[0].split(".")

        val length = maxOf(parts1.size, parts2.size)
        for (i in 0 until length) {
            val p1 = parts1.getOrNull(i)?.toIntOrNull() ?: 0
            val p2 = parts2.getOrNull(i)?.toIntOrNull() ?: 0
            if (p1 != p2) return p1 - p2
        }

        // Handle pre-release tags (alpha, beta, rc)
        val pre1 = main1.getOrNull(1) ?: ""
        val pre2 = main2.getOrNull(1) ?: ""
        if (pre1.isEmpty() && pre2.isNotEmpty()) return 1  // release > pre-release
        if (pre1.isNotEmpty() && pre2.isEmpty()) return -1 // pre-release < release

        return pre1.compareTo(pre2) // lexicographic compare ("alpha" < "beta")
    }
}