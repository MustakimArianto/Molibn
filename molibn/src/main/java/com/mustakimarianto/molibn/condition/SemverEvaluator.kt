package com.mustakimarianto.molibn.condition

import android.util.Log
import java.util.Locale

/**
 * Utility class to evaluate semantic version conditions against a given app version.
 *
 * Supports:
 * - `>=1.2.0`
 * - `>1.0.0`
 * - `<=3.0.0`
 * - `<2.5.0`
 * - `Exact string match (=2.0-beta)`
 *
 * Fails gracefully: if parsing fails, returns false.
 */
internal object SemverEvaluator {
    private const val TAG = "SemverEvaluator"

    /**
     * Evaluate a version condition against the current app version.
     *
     * @param appVersion the current version string (e.g., "2.1.0")
     * @param condition a condition string (e.g., ">=2.0.0", "<3.0.0", "=2.0-beta")
     * @return true if condition passes, false otherwise
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
                    val parts = conditionTrimmed.split("-").map { it.trim() }
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
     * Compare two semver strings.
     *
     * @return negative if v1 < v2, zero if equal, positive if v1 > v2
     */
    private fun compare(v1: String, v2: String): Int {
        val parts1 = v1.split(".")
        val parts2 = v2.split(".")

        val length = maxOf(parts1.size, parts2.size)
        for (i in 0 until length) {
            val p1 = parts1.getOrNull(i)?.toIntOrNull() ?: 0
            val p2 = parts2.getOrNull(i)?.toIntOrNull() ?: 0
            if (p1 != p2) return p1 - p2
        }
        return 0
    }
}