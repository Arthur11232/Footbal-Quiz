package com.arthuralexandryan.footballquiz.domain.sync

import kotlin.test.Test
import kotlin.test.assertEquals

class SyncDecisionResolverTest {
    @Test
    fun emptyLocalAndMissingCloudDoNothing() {
        assertEquals(SyncDecision.NONE, SyncDecisionResolver.resolve(local(false), null))
    }

    @Test
    fun localProgressAndMissingCloudUploadsLocal() {
        assertEquals(SyncDecision.UPLOAD_LOCAL, SyncDecisionResolver.resolve(local(true), null))
    }

    @Test
    fun emptyCloudUsesLocalProgress() {
        assertEquals(
            SyncDecision.UPLOAD_LOCAL,
            SyncDecisionResolver.resolve(local(true), cloud(false))
        )
        assertEquals(SyncDecision.NONE, SyncDecisionResolver.resolve(local(false), cloud(false)))
    }

    @Test
    fun cloudProgressAndEmptyLocalAutoRestores() {
        assertEquals(
            SyncDecision.AUTO_RESTORE_CLOUD,
            SyncDecisionResolver.resolve(local(false), cloud(true))
        )
    }

    @Test
    fun matchingProgressDoesNothing() {
        assertEquals(SyncDecision.NONE, SyncDecisionResolver.resolve(local(true), cloud(true)))
    }

    @Test
    fun changedScorePromptsForConflict() {
        assertEquals(
            SyncDecision.PROMPT_CONFLICT,
            SyncDecisionResolver.resolve(local(true), cloud(true, score = 11))
        )
    }

    @Test
    fun changedSyncTimePromptsForConflict() {
        assertEquals(
            SyncDecision.PROMPT_CONFLICT,
            SyncDecisionResolver.resolve(local(true), cloud(true, timestamp = 2L))
        )
    }

    private fun local(hasProgress: Boolean) = LocalProgressSummary(
        hasProgress = hasProgress,
        totalScore = 10,
        lastSynced = 1L
    )

    private fun cloud(hasProgress: Boolean, score: Int = 10, timestamp: Long = 1L) =
        CloudProgressSummary(
            hasProgress = hasProgress,
            totalScore = score,
            lastSynced = timestamp
        )
}
