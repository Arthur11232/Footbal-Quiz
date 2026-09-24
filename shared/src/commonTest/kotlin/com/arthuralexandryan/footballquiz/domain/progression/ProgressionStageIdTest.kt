package com.arthuralexandryan.footballquiz.domain.progression

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProgressionStageIdTest {
    @Test
    fun persistedValuesResolveToTheSameStage() {
        ProgressionStageId.entries.forEach { stage ->
            assertEquals(stage, ProgressionStageId.fromValue(stage.value))
        }
    }

    @Test
    fun missingOrUnknownValuesDoNotResolve() {
        assertNull(ProgressionStageId.fromValue(null))
        assertNull(ProgressionStageId.fromValue(""))
        assertNull(ProgressionStageId.fromValue("unknown_stage"))
    }
}
