package com.mustakimarianto.molibn

import android.content.Context
import io.mockk.mockk
import org.junit.Before

abstract class BaseMolibnTest {
    protected lateinit var molibn: Molibn
    protected lateinit var context: Context

    @Before
    fun baseSetup() {
        context = mockk(relaxed = true)
        molibn = Molibn.Builder(context)
            .setCacheEnabled(false)
            .build()
    }
}