package com.mustakimarianto.molibn

import com.mustakimarianto.molibn.model.ConditionModel
import com.mustakimarianto.molibn.model.FeatureModel
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class SemverPreReleaseTagConditionTest : BaseMolibnTest() {
    private val currentVersionAlpha = "1.0.0-alpha"
    private val currentVersionBeta = "1.0.0-beta"
    private val currentVersionRc = "1.0.0-rc"
    private val currentVersionRelease = "1.0.0"

    @Test
    fun `alpha satisfies gte alpha condition`() {
        val feature = FeatureModel(
            "feat_alpha", true,
            ConditionModel(
                supportedApiLevels = listOf(),
                supportedAppVersions = listOf(">=1.0.0-alpha")
            )
        )
        molibn.saveSingleFeature(feature)
        assertTrue(molibn.isSupportedAppVersion("feat_alpha", currentVersionAlpha))
    }

    @Test
    fun `alpha does not gt beta condition`() {
        val feature = FeatureModel(
            "feat_alpha2", true,
            ConditionModel(
                supportedApiLevels = listOf(),
                supportedAppVersions = listOf(">1.0.0-beta")
            )
        )
        molibn.saveSingleFeature(feature)
        assertFalse(molibn.isSupportedAppVersion("feat_alpha2", currentVersionAlpha))
    }

    @Test
    fun `beta satisfies gt alpha condition`() {
        val feature = FeatureModel(
            "feat_beta", true,
            ConditionModel(
                supportedApiLevels = listOf(),
                supportedAppVersions = listOf(">1.0.0-alpha")
            )
        )
        molibn.saveSingleFeature(feature)
        assertTrue(molibn.isSupportedAppVersion("feat_beta", currentVersionBeta))
    }

    @Test
    fun `rc satisfies gte beta condition`() {
        val feature = FeatureModel(
            "feat_rc", true,
            ConditionModel(
                supportedApiLevels = listOf(),
                supportedAppVersions = listOf(">=1.0.0-beta")
            )
        )
        molibn.saveSingleFeature(feature)
        assertTrue(molibn.isSupportedAppVersion("feat_rc", currentVersionRc))
    }

    @Test
    fun `release satisfies gte rc condition`() {
        val feature = FeatureModel(
            "feat_release", true,
            ConditionModel(
                supportedApiLevels = listOf(),
                supportedAppVersions = listOf(">=1.0.0-rc")
            )
        )
        molibn.saveSingleFeature(feature)
        assertTrue(molibn.isSupportedAppVersion("feat_release", currentVersionRelease))
    }
}
