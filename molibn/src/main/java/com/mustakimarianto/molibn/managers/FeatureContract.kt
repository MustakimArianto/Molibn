package com.mustakimarianto.molibn.managers

import com.mustakimarianto.molibn.model.FeatureModel
import kotlinx.coroutines.flow.Flow

internal interface FeatureContract {
    fun saveSingleFeature(featureModel: FeatureModel)
    fun saveMultipleFeature(featureModels: List<FeatureModel>)
    fun updateFeature(featureModel: FeatureModel)
    fun getAllFeatures(): List<FeatureModel>
    fun getFeature(flag: String, defaultValue: Boolean = false): Boolean
    fun observeFeature(flag: String): Flow<Boolean>
    fun isEnabled(flag: String): Boolean
    fun getEnabledFeatures(): List<FeatureModel>
    fun getDisabledFeatures(): List<FeatureModel>
    fun hasEnabledFeatures(): Boolean
    fun getSupportedApiLevel(featureName: String): List<String>
    fun isSupportedApiLevel(featureName: String): Boolean
    fun getSupportedAppVersions(featureName: String): List<String>
    fun isSupportedAppVersion(featureName: String, currentVersion: String): Boolean
    fun clearAllFlags()
}