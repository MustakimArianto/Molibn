package com.mustakimarianto.molibn.conditions

import android.util.Log
import com.mustakimarianto.molibn.core.SdkProvider

/**
 * Utility class that evaluates string-based API level conditions against the current device's SDK level.
 *
 * This class supports common comparison formats to check whether the current
 * Android API level (from [SdkProvider.sdkInt]) satisfies the given condition.
 *
 * Supported condition formats:
 * - `">=29"` → true if the current API level is greater than or equal to 29
 * - `">29"` → true if the current API level is strictly greater than 29
 * - "`<=33"` → true if the current API level is less than or equal to 33
 * - "`<33"` → true if the current API level is strictly less than 33
 * - `"21-30"` → true if the current API level is within the inclusive range [21, 30]
 * - `"30"` → true if the current API level is exactly 30
 *
 * Any invalid or unparsable condition will return `false` and log a warning.
 *
 * @constructor Accepts an [SdkProvider] instance, which provides the current SDK level.
 */
internal class ApiLevelEvaluator(sdkProvider: SdkProvider) {
    companion object {
        private const val TAG = "ApiLevelEvaluator"
    }

    /**
     * Evaluates the given API level condition string against the current SDK level.
     *
     * @param condition A string that specifies the API level condition.
     * @return `true` if the current SDK level satisfies the condition, otherwise `false`.
     *
     * Example usage:
     * ```
     * evaluate(">=29")   // true if current API level >= 29
     * evaluate("21-30")  // true if current API level is between 21 and 30 (inclusive)
     * evaluate("30")     // true if current API level == 30
     * ```
     */
    fun evaluate(condition: String): Boolean {
        val currentSdk = SdkProvider.sdkInt

        return try {
            when {
                condition.startsWith(">=") -> {
                    val min = condition.removePrefix(">=").trim().toIntOrNull()
                    min != null && currentSdk >= min
                }

                condition.startsWith(">") -> {
                    val min = condition.removePrefix(">").trim().toIntOrNull()
                    min != null && currentSdk > min

                }

                condition.startsWith("<=") -> {
                    val max = condition.removePrefix("<=").trim().toIntOrNull()
                    max != null && currentSdk <= max
                }

                condition.startsWith("<") -> {
                    val max = condition.removePrefix("<").trim().toIntOrNull()
                    max != null && currentSdk < max
                }

                "-" in condition -> {
                    val parts = condition.split("-").mapNotNull { it.trim().toIntOrNull() }
                    if (parts.size == 2) {
                        val (min, max) = parts
                        currentSdk in min..max
                    } else {
                        false
                    }
                }

                else -> {
                    val exact = condition.trim().toIntOrNull()
                    exact != null && currentSdk == exact
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to evaluate semver condition: $condition", e)
            false
        }
    }
}