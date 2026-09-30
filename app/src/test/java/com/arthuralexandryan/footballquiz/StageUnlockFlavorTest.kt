package com.arthuralexandryan.footballquiz

import org.junit.Assert.assertEquals
import org.junit.Test

class StageUnlockFlavorTest {
    @Test
    fun onlyQaFlavorBypassesStageLocks() {
        assertEquals(BuildConfig.APP_ENV == "qa", BuildConfig.UNLOCK_ALL_STAGES)
    }
}
