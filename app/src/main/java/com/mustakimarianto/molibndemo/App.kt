package com.mustakimarianto.molibndemo

import android.app.Application
import android.util.Log
import com.mustakimarianto.molibn.Molibn
import com.mustakimarianto.molibn.model.FeatureModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class App : Application() {
    companion object {
        private const val TAG = "App"
    }

    var molibn: Molibn? = null

    override fun onCreate() {
        super.onCreate()

        val features = mutableListOf(
            FeatureModel("feature1", true, ">=24"),
            FeatureModel("feature2", false, ">=24"),
            FeatureModel("feature3", true, "<24")
        )

        molibn =
            Molibn.Builder(context = applicationContext)
                .setCacheEnabled(true)
                .build()

        molibn?.saveMultipleFeature(features)
        molibn?.let {
            Log.d(TAG, "All features: ${it.getAllFeatures()}")
            Log.d(TAG, "Feature 1 is enabled: ${it.isEnabled("feature1")}")
            Log.d(TAG, "Feature 2 is enabled: ${it.isEnabled("feature2")}")
            Log.d(TAG, "Feature 3 is enabled: ${it.isEnabled("feature3")}")
            Log.d(TAG, "Adding feature 4")
            it.saveSingleFeature(FeatureModel("feature4", false, ">=24"))
            Log.d(TAG, "Get feature 4: ${it.getFeature("feature4")}")
            Log.d(TAG, "Get non-existent feature: ${it.getFeature("feature5")}")
            Log.d(TAG, "Is non-existent feature enabled: ${it.getFeature("feature5")}")
            Log.d(TAG, "All features: ${it.getAllFeatures()}")
            Log.d(TAG, "Enabled flags: ${it.getEnabledFeatures()}")
            Log.d(TAG, "Disabled flags: ${it.getDisabledFeatures()}")
            Log.d(TAG, "Has enabled flags: ${it.hasEnabledFeatures()}")

            CoroutineScope(Dispatchers.IO).launch {
                launch {
                    it.observeFeature("feature4").collect { isEnabled ->
                        Log.d(TAG, "Feature 4 is now $isEnabled")
                    }
                }

                launch {
                    delay(3000)
                    it.updateFeature(FeatureModel("feature4", true, ">=24"))
                    delay(3000)
                    it.updateFeature(FeatureModel("feature4", false, ">=24"))
                }
            }
            it.clearAllFlags()
            Log.d(TAG, "All features after clear: ${it.getAllFeatures()}")
        }
    }
}