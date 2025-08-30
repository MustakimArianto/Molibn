package com.mustakimarianto.molibn

import com.mustakimarianto.molibn.model.ConditionModel
import com.mustakimarianto.molibn.model.FeatureModel
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class SemverConditionTest : BaseMolibnTest() {
    private val currentVersion = "1.0.0"

    @Test
    fun `return true for greater than or equal condition`() {
        with(molibn) {
            val featureName = "test_feature1"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf(),
                    supportedAppVersions = listOf(">=1.0.0")
                )
            )

            saveSingleFeature(model)
            assertTrue(isSupportedAppVersion(featureName, currentVersion))
        }
    }

    @Test
    fun `return false for greater than or equal condition`() {
        with(molibn) {
            val featureName = "test_feature2"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf(),
                    supportedAppVersions = listOf(">=1.0.1")
                )
            )

            saveSingleFeature(model)
            assertFalse(isSupportedAppVersion(featureName, currentVersion))
        }
    }

    @Test
    fun `return true for greater than condition`() {
        with(molibn) {
            val featureName = "test_feature3"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf(),
                    supportedAppVersions = listOf(">0.9.0")
                )
            )

            saveSingleFeature(model)
            assertTrue(isSupportedAppVersion(featureName, currentVersion))
        }
    }

    @Test
    fun `return false for greater than condition`() {
        with(molibn) {
            val featureName = "test_feature4"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf(),
                    supportedAppVersions = listOf("<0.9.0")
                )
            )

            saveSingleFeature(model)
            assertFalse(isSupportedAppVersion(featureName, currentVersion))
        }
    }

    @Test
    fun `return true for less than or equal condition`() {
        with(molibn) {
            val featureName = "test_feature5"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf(),
                    supportedAppVersions = listOf("<=1.0.0")
                )
            )

            saveSingleFeature(model)
            assertTrue(isSupportedAppVersion(featureName, currentVersion))
        }
    }

    @Test
    fun `return false for less than or equal condition`() {
        with(molibn) {
            val featureName = "test_feature6"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf(),
                    supportedAppVersions = listOf("<=0.9.0")
                )
            )

            saveSingleFeature(model)
            assertFalse(isSupportedAppVersion(featureName, currentVersion))
        }
    }

    @Test
    fun `return true for less than condition`() {
        with(molibn) {
            val featureName = "test_feature7"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf(),
                    supportedAppVersions = listOf("<1.0.1")
                )
            )
            saveSingleFeature(model)
            assertTrue(isSupportedAppVersion(featureName, currentVersion))
        }
    }

    @Test
    fun `return false for less than condition`() {
        with(molibn) {
            val featureName = "test_feature8"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf(),
                    supportedAppVersions = listOf("<1.0.0")
                )
            )

            saveSingleFeature(model)
            assertFalse(isSupportedAppVersion(featureName, currentVersion))
        }
    }

    @Test
    fun `return true for range condition`() {
        with(molibn) {
            val featureName = "test_feature9"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf(),
                    supportedAppVersions = listOf("0.9.0-1.0.0")
                )
            )

            saveSingleFeature(model)
            assertTrue(isSupportedAppVersion(featureName, currentVersion))
        }
    }

    @Test
    fun `return false for range condition`() {
        with(molibn) {
            val featureName = "test_feature10"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf(),
                    supportedAppVersions = listOf("1.1.0-1.2.0")
                )
            )

            saveSingleFeature(model)
            assertFalse(isSupportedAppVersion(featureName, currentVersion))
        }
    }

    @Test
    fun `return true for exact condition`() {
        with(molibn) {
            val featureName = "test_feature11"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf(),
                    supportedAppVersions = listOf("1.0.0")
                )
            )

            saveSingleFeature(model)
            assertTrue(isSupportedAppVersion(featureName, currentVersion))
        }
    }

    @Test
    fun `return false for exact condition`() {
        with(molibn) {
            val featureName = "test_feature12"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf(),
                    supportedAppVersions = listOf("1.1.0")
                )
            )

            saveSingleFeature(model)
            assertFalse(isSupportedAppVersion(featureName, currentVersion))
        }
    }
}