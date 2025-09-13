package com.mustakimarianto.molibn.managers

import com.mustakimarianto.molibn.conditions.ApiLevelEvaluator
import com.mustakimarianto.molibn.conditions.AppVersionEvaluator
import com.mustakimarianto.molibn.core.SdkProvider
import com.mustakimarianto.molibn.model.FeatureModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages feature flags and conditions for enabling/disabling features dynamically.
 *
 * Responsibilities:
 * - Store feature states in memory.
 * - Provide CRUD operations on feature flags.
 * - Evaluate feature availability based on API level or app version.
 * - Expose reactive observation through [Flow].
 */
internal class FeatureManager : FeatureContract {
    private val apiLevelEvaluator = ApiLevelEvaluator(sdkProvider = SdkProvider)
    private val appVersionEvaluator = AppVersionEvaluator()

    /** In-memory list of all features and their states. */
    val featureState = mutableListOf<FeatureModel>()

    /** Reactive flows for observing feature flag changes by name. */
    val flagStates = mutableMapOf<String, MutableStateFlow<Boolean>>()

    /**
     * Saves a single feature into the in-memory feature state.
     *
     * @param featureModel The feature to be stored.
     */
    override fun saveSingleFeature(featureModel: FeatureModel) {
        featureState.add(featureModel)
    }

    /**
     * Saves multiple features into the in-memory feature state.
     *
     * @param featureModels List of features to be stored.
     */
    override fun saveMultipleFeature(featureModels: List<FeatureModel>) {
        featureState.addAll(featureModels)
    }

    /**
     * Updates an existing feature if present, otherwise inserts it.
     * Also updates its reactive [Flow] state if applicable.
     *
     * @param featureModel The feature to update or insert.
     */
    override fun updateFeature(featureModel: FeatureModel) {
        val index = featureState.indexOfFirst { it.name == featureModel.name }
        if (index >= 0) {
            featureState[index] = featureModel
        } else {
            featureState.add(featureModel)
        }

        val flow = flagStates.getOrPut(featureModel.name) {
            MutableStateFlow(featureModel.enabled)
        }
        flow.value = featureModel.enabled
    }

    /**
     * Retrieves all features currently stored in memory.
     *
     * @return A list of all [FeatureModel]s.
     */
    override fun getAllFeatures(): List<FeatureModel> {
        return featureState
    }

    /**
     * Retrieves the state of a specific feature flag.
     *
     * @param flag The feature flag name.
     * @param defaultValue The value to return if the flag does not exist.
     * @return True if enabled, false if disabled, or [defaultValue] if not found.
     */
    override fun getFeature(flag: String, defaultValue: Boolean): Boolean {
        return featureState
            .find { it.name == flag }
            ?.enabled ?: defaultValue
    }

    /**
     * Observes changes of a specific feature flag reactively.
     *
     * @param flag The feature flag name.
     * @return A [Flow] that emits the feature's enabled state.
     */
    override fun observeFeature(flag: String): Flow<Boolean> {
        val feature = featureState.find { it.name == flag }
        val flow = flagStates.getOrPut(flag) {
            MutableStateFlow(feature?.enabled ?: false)
        }
        return flow.asStateFlow()
    }

    /**
     * Checks if a specific feature flag is enabled.
     *
     * @param flag The feature flag name.
     * @return True if enabled, false otherwise.
     */
    override fun isEnabled(flag: String): Boolean {
        return featureState
            .find { it.name == flag }
            ?.enabled ?: false
    }

    /**
     * Retrieves all features that are currently enabled.
     *
     * @return A list of enabled [FeatureModel]s.
     */
    override fun getEnabledFeatures(): List<FeatureModel> {
        return featureState.filter { it.enabled }.toList()
    }

    /**
     * Retrieves all features that are currently disabled.
     *
     * @return A list of disabled [FeatureModel]s.
     */
    override fun getDisabledFeatures(): List<FeatureModel> {
        return featureState.filter { !it.enabled }.toList()
    }

    /**
     * Checks if there is at least one enabled feature.
     *
     * @return True if at least one feature is enabled, false otherwise.
     */
    override fun hasEnabledFeatures(): Boolean {
        return featureState.any { it.enabled }
    }


    /**
     * Gets the list of supported API level rules for a feature.
     *
     * @param featureName The name of the feature.
     * @return A list of API level rules (e.g., ">=29", "21-30").
     */
    override fun getSupportedApiLevel(featureName: String): List<String> {
        return featureState.firstOrNull { it.name == featureName }?.condition?.supportedApiLevels
            ?: emptyList()
    }

    /**
     * Checks if the current SDK level satisfies the feature's supported API level rules.
     *
     * @param featureName The name of the feature.
     * @return True if supported, false otherwise.
     */
    override fun isSupportedApiLevel(featureName: String): Boolean {
        val supportedApiLevels = getSupportedApiLevel(featureName)
        if (supportedApiLevels.isEmpty()) return true // no restriction → allow by default

        return supportedApiLevels.any { rule ->
            apiLevelEvaluator.evaluate(rule)
        }
    }

    /**
     * Gets the list of supported app version rules for a feature.
     *
     * @param featureName The name of the feature.
     * @return A list of version rules (e.g., ">=1.2.0", "1.0.0-2.0.0").
     */
    override fun getSupportedAppVersions(featureName: String): List<String> {
        return featureState.firstOrNull { it.name == featureName }?.condition?.supportedAppVersions
            ?: emptyList()
    }

    /**
     * Checks if the current app version satisfies the feature's supported version rules.
     *
     * @param featureName The name of the feature.
     * @param currentVersion The app version to check against.
     * @return True if supported, false otherwise.
     */
    override fun isSupportedAppVersion(
        featureName: String,
        currentVersion: String
    ): Boolean {
        val supportedAppVersions = getSupportedAppVersions(featureName)
        if (supportedAppVersions.isEmpty()) return true // no restriction → allow by default

        return supportedAppVersions.any { rule ->
            appVersionEvaluator.evaluate(currentVersion, rule)
        }
    }

    /**
     * Clears all stored feature flags and resets state.
     */
    override fun clearAllFlags() {
        featureState.clear()
    }
}