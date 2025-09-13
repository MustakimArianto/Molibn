package com.mustakimarianto.molibn

import com.mustakimarianto.molibn.core.SdkProvider
import com.mustakimarianto.molibn.model.ConditionModel
import com.mustakimarianto.molibn.model.FeatureModel
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class ApiLevelConditionTest : BaseMolibnTest() {

    @Before
    fun setup() {
        SdkProvider.sdkInt = 29
    }

    @Test
    fun `return true for greater than or equal condition`() {
        with(molibn) {
            val featureName = "test_feature1"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf(">=23"),
                    supportedAppVersions = listOf()
                )
            )

            saveSingleFeature(model)
            assertTrue(isSupportedApiLevel(featureName))
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
                    supportedApiLevels = listOf(">=30"),
                    supportedAppVersions = listOf()
                )
            )

            saveSingleFeature(model)
            assertFalse(isSupportedApiLevel(featureName))
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
                    supportedApiLevels = listOf(">23"),
                    supportedAppVersions = listOf()
                )
            )

            saveSingleFeature(model)
            assertTrue(isSupportedApiLevel(featureName))
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
                    supportedApiLevels = listOf(">29"),
                    supportedAppVersions = listOf()
                )
            )

            saveSingleFeature(model)
            assertFalse(isSupportedApiLevel(featureName))
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
                    supportedApiLevels = listOf("<=29"),
                    supportedAppVersions = listOf()
                )
            )

            saveSingleFeature(model)
            assertTrue(isSupportedApiLevel(featureName))
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
                    supportedApiLevels = listOf("<=28"),
                    supportedAppVersions = listOf()
                )
            )

            saveSingleFeature(model)
            assertFalse(isSupportedApiLevel(featureName))
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
                    supportedApiLevels = listOf("<30"),
                    supportedAppVersions = listOf()
                )
            )
            saveSingleFeature(model)
            assertTrue(isSupportedApiLevel(featureName))
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
                    supportedApiLevels = listOf("<29"),
                    supportedAppVersions = listOf()
                )
            )
            saveSingleFeature(model)
            assertFalse(isSupportedApiLevel(featureName))
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
                    supportedApiLevels = listOf("23-30"),
                    supportedAppVersions = listOf()
                )
            )

            saveSingleFeature(model)
            assertTrue(isSupportedApiLevel(featureName))
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
                    supportedApiLevels = listOf("30-36"),
                    supportedAppVersions = listOf()
                )
            )

            saveSingleFeature(model)
            assertFalse(isSupportedApiLevel(featureName))
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
                    supportedApiLevels = listOf("29"),
                    supportedAppVersions = listOf()
                )
            )

            saveSingleFeature(model)
            assertTrue(isSupportedApiLevel(featureName))
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
                    supportedApiLevels = listOf("30"),
                    supportedAppVersions = listOf()
                )
            )

            saveSingleFeature(model)
            assertFalse(isSupportedApiLevel(featureName))
        }
    }

    @Test
    fun `return true for combination condition`() {
        with(molibn) {
            val featureName = "test_feature13"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf("<=23", "29", "31-33", ">=34"),
                    supportedAppVersions = listOf()
                )
            )

            saveSingleFeature(model)
            assertTrue(isSupportedApiLevel(featureName))
        }
    }

    @Test
    fun `return false for combination condition`() {
        with(molibn) {
            val featureName = "test_feature13"
            val model = FeatureModel(
                name = featureName,
                enabled = true,
                condition = ConditionModel(
                    supportedApiLevels = listOf("<=23", "28", "31-33", ">=34"),
                    supportedAppVersions = listOf()
                )
            )

            saveSingleFeature(model)
            assertFalse(isSupportedApiLevel(featureName))
        }
    }
}