package com.arthuralexandryan.footballquiz.shared

import kotlin.test.Test
import kotlin.test.assertEquals

class SharedBuildInfoTest {
    @Test
    fun exposesStableFrameworkName() {
        assertEquals("FootballQuizShared", SharedBuildInfo.name)
    }
}
