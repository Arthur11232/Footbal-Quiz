package com.arthuralexandryan.footballquiz.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SharedDiagnosticsTest {
    @Test
    fun exposesStageCatalogAndCompletionRule() {
        val diagnostics = SharedDiagnostics()

        assertEquals("FootballQuizShared", diagnostics.moduleName)
        assertEquals(16, diagnostics.stageCount)
        assertTrue(diagnostics.stageIdsText.contains("top5_france_complete"))
        assertFalse(diagnostics.isFranceComplete(39))
        assertTrue(diagnostics.isFranceComplete(40))
    }
}
