package com.mustakimarianto.molibn.model

import android.content.Context

data class MolibnConfigModel(
    val context: Context,
    val cacheEnabled: Boolean = true,
)