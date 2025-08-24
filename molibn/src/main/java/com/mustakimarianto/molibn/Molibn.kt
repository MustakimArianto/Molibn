package com.mustakimarianto.molibn

import android.content.Context
import android.os.Build
import com.mustakimarianto.molibn.model.FeatureModel
import com.mustakimarianto.molibn.model.MolibnConfigModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Molibn private constructor(config: MolibnConfigModel) {
    // Mutable list of current features
    private val featureState = mutableListOf<FeatureModel>()

    // Flows for reactive observation
    private val flagStates = mutableMapOf<String, MutableStateFlow<Boolean>>()

    companion object {
        @Volatile
        private var instance: Molibn? = null

        fun getInstance(): Molibn {
            return instance ?: throw IllegalStateException(
                "Molibn not initialized. Call Molibn.Builder(...).build() first."
            )
        }
    }

    init {
        if (config.cacheEnabled) {
            loadCachedFeatures()
        }

        // Initialize state flows for all feature from featureState
        featureState.forEach { feature ->
            flagStates[feature.name] = MutableStateFlow(feature.enabled)
        }
    }

    /**
     * Check if a specific feature is enabled
     */
    fun isEnabled(flag: String): Boolean {
        return featureState
            .find { it.name == flag }
            ?.enabled ?: false
    }

    /**
     * Get all feature
     */
    fun getAllFeatures(): List<FeatureModel> {
        return featureState
    }

    /**
     * Get a specific feature flag value with default fallback
     */
    fun getFeature(flag: String, defaultValue: Boolean = false): Boolean {
        return featureState
            .find { it.name == flag }
            ?.enabled ?: defaultValue
    }

    /**
     * Observe changes to a specific feature
     */
    fun observeFeature(flag: String): Flow<Boolean> {
        val feature = featureState.find { it.name == flag }
        val flow = flagStates.getOrPut(flag) {
            MutableStateFlow(feature?.enabled ?: false)
        }
        return flow.asStateFlow()
    }

    /**
     * Get all features that are currently enabled
     */
    fun getEnabledFeatures(): List<FeatureModel> {
        return featureState.filter { it.enabled }.toList()
    }

    /**
     * Get all features that are currently disabled
     */
    fun getDisabledFeatures(): List<FeatureModel> {
        return featureState.filter { !it.enabled }.toList()
    }

    fun getSupportedApiLevel(featureName: String): List<Int> {
        return featureState.firstOrNull { it.name == featureName }?.supportedApiLevels
            ?: emptyList()
    }

    fun isSupportedApiLevel(featureName: String): Boolean {
        return getSupportedApiLevel(featureName).contains(Build.VERSION.SDK_INT)
    }

    /**
     * Check if any features are enabled
     */
    fun hasEnabledFeatures(): Boolean {
        return featureState.any { it.enabled }
    }

    /**
     * Clear all feature
     */
    fun clearAllFlags() {
        featureState.clear()
    }

    /**
     * Save a single feature
     */
    fun saveSingleFeature(featureModel: FeatureModel) {
        featureState.add(featureModel)
    }

    /**
     * Save multiple features
     */
    fun saveMultipleFeature(featureModels: List<FeatureModel>) {
        featureState.addAll(featureModels)
    }

    /**
     * Update existing feature or insert if not exists
     */
    fun updateFeature(featureModel: FeatureModel) {
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

    fun loadCachedFeatures() {
        // Implementation for loading cached flags from SharedPreferences or Room
        // This would merge cached values with the provided configuration
    }

    class Builder(private val context: Context) {
        private var cacheEnabled: Boolean = true

        fun setCacheEnabled(enabled: Boolean) = apply { this.cacheEnabled = enabled }

        fun build(): Molibn {
            val config = MolibnConfigModel(
                context, cacheEnabled = cacheEnabled
            )
            return Molibn(config).also { instance = it }
        }
    }
}