package com.mustakimarianto.molibn

import android.content.Context
import com.mustakimarianto.molibn.managers.CacheManager
import com.mustakimarianto.molibn.managers.FeatureManager
import com.mustakimarianto.molibn.model.FeatureModel
import com.mustakimarianto.molibn.model.MolibnConfigModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class Molibn private constructor(config: MolibnConfigModel) {
    private val featureManager = FeatureManager()
    private val cacheManager: CacheManager? =
        if (config.cacheEnabled) CacheManager(config.context) else null

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
        with(featureManager) {
            featureState.forEach { feature ->
                flagStates[feature.name] = MutableStateFlow(feature.enabled)
            }
        }

    }

    /** See [FeatureManager.isEnabled] for details. */
    fun isEnabled(flag: String): Boolean {
        return featureManager.isEnabled(flag)
    }

    /** See [FeatureManager.getAllFeatures] for details. */
    fun getAllFeatures(): List<FeatureModel> {
        return featureManager.getAllFeatures()
    }

    /** See [FeatureManager.getFeature] for details. */
    fun getFeature(flag: String, defaultValue: Boolean = false): Boolean {
        return featureManager.getFeature(flag, defaultValue)
    }

    /** See [FeatureManager.observeFeature] for details. */
    fun observeFeature(flag: String): Flow<Boolean> {
        return featureManager.observeFeature(flag)
    }

    /** See [FeatureManager.getEnabledFeatures] for details. */
    fun getEnabledFeatures(): List<FeatureModel> {
        return featureManager.getEnabledFeatures()
    }

    /** See [FeatureManager.getDisabledFeatures] for details. */
    fun getDisabledFeatures(): List<FeatureModel> {
        return featureManager.getDisabledFeatures()
    }

    /** See [FeatureManager.getSupportedApiLevel] for details. */
    fun getSupportedApiLevel(featureName: String): List<String> {
        return featureManager.getSupportedApiLevel(featureName)
    }

    /** See [FeatureManager.isSupportedApiLevel] for details. */
    fun isSupportedApiLevel(featureName: String): Boolean {
        return featureManager.isSupportedApiLevel(featureName)
    }

    /** See [FeatureManager.getSupportedAppVersions] for details. */
    fun getSupportedAppVersions(featureName: String): List<String> {
        return featureManager.getSupportedAppVersions(featureName)
    }

    /** See [FeatureManager.isSupportedAppVersion] for details. */
    fun isSupportedAppVersion(featureName: String, currentVersion: String): Boolean {
        return featureManager.isSupportedAppVersion(featureName, currentVersion)
    }

    /** See [FeatureManager.hasEnabledFeatures] for details. */
    fun hasEnabledFeatures(): Boolean {
        return featureManager.hasEnabledFeatures()
    }

    /** See [FeatureManager.clearAllFlags] for details. */
    fun clearAllFlags() {
        return featureManager.clearAllFlags()
    }

    /** See [FeatureManager.saveSingleFeature] for details. */
    fun saveSingleFeature(featureModel: FeatureModel) {
        featureManager.saveSingleFeature(featureModel)
    }

    /** See [FeatureManager.saveMultipleFeature] for details. */
    fun saveMultipleFeature(featureModels: List<FeatureModel>) {
        featureManager.saveMultipleFeature(featureModels)
    }

    /** See [FeatureManager.updateFeature] for details. */
    fun updateFeature(featureModel: FeatureModel) {
        featureManager.updateFeature(featureModel)
    }

    /** See [CacheManager.loadCacheFeature] for details. */
    fun loadCachedFeatures() {
        cacheManager?.loadCacheFeature()
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