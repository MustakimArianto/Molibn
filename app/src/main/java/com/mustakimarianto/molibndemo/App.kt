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
            FeatureModel(
                "feature1",
                true,
                listOf(24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36)
            ),
            FeatureModel("feature2", false, listOf(28, 29, 30, 31, 32)),
            FeatureModel("feature3", true, listOf(32))
        )

        molibn = Molibn.Builder(context = applicationContext).setCacheEnabled(true).build()

        molibn?.saveMultipleFeature(features)
        molibn?.let {
            Log.d(TAG, "All features: ${it.getAllFeatures()}")
            Log.d(TAG, "Feature 1 is enabled: ${it.isEnabled("feature1")}")
            Log.d(TAG, "Feature 2 is enabled: ${it.isEnabled("feature2")}")
            Log.d(TAG, "Feature 3 is enabled: ${it.isEnabled("feature3")}")
            Log.d(TAG, "Feature 1 supported api: ${it.getSupportedApiLevel("feature1")}")
            Log.d(TAG, "Feature 2 supported api: ${it.getSupportedApiLevel("feature2")}")
            Log.d(TAG, "Feature 3 supported api: ${it.getSupportedApiLevel("feature3")}")
            Log.d(TAG, "Feature 4 supported api: ${it.getSupportedApiLevel("feature4")}")
            Log.d(
                TAG,
                "Is current device supported feature 1: ${it.isSupportedApiLevel("feature1")}"
            )
            Log.d(
                TAG,
                "Is current device supported feature 2: ${it.isSupportedApiLevel("feature2")}"
            )
            Log.d(
                TAG,
                "Is current device supported feature 3: ${it.isSupportedApiLevel("feature3")}"
            )
            Log.d(
                TAG,
                "Is current device supported feature 4: ${it.isSupportedApiLevel("feature4")}"
            )
            Log.d(TAG, "Adding feature 4")
            it.saveSingleFeature(FeatureModel("feature4", false, listOf(31, 32, 33, 34)))
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
                    it.updateFeature(FeatureModel("feature4", true, listOf(33, 34)))
                    delay(3000)
                    it.updateFeature(FeatureModel("feature4", false, listOf(34)))
                }
            }
            it.clearAllFlags()
            Log.d(TAG, "All features after clear: ${it.getAllFeatures()}")
        }
    }
}