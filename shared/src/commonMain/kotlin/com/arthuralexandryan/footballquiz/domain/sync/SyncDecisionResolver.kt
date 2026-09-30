package com.arthuralexandryan.footballquiz.domain.sync

data class LocalProgressSummary(
    val hasProgress: Boolean,
    val totalScore: Int,
    val lastSynced: Long
)

data class CloudProgressSummary(
    val hasProgress: Boolean,
    val totalScore: Int,
    val lastSynced: Long
)

enum class SyncDecision {
    NONE,
    AUTO_RESTORE_CLOUD,
    PROMPT_CONFLICT,
    UPLOAD_LOCAL
}

object SyncDecisionResolver {
    fun resolve(
        local: LocalProgressSummary,
        cloud: CloudProgressSummary?
    ): SyncDecision {
        if (cloud == null || !cloud.hasProgress) {
            return if (local.hasProgress) SyncDecision.UPLOAD_LOCAL else SyncDecision.NONE
        }
        if (!local.hasProgress) return SyncDecision.AUTO_RESTORE_CLOUD

        return if (local.totalScore == cloud.totalScore && local.lastSynced == cloud.lastSynced) {
            SyncDecision.NONE
        } else {
            SyncDecision.PROMPT_CONFLICT
        }
    }
}
