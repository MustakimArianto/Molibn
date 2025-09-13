package com.mustakimarianto.molibn.model

data class FeatureModel(
    val name: String,
    val enabled: Boolean,
    val condition: ConditionModel
)