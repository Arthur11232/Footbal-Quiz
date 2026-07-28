package com.arthuralexandryan.footballquiz.data.repository

import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.domain.reward.PendingRewardRecord

interface PendingRewardRepository {
    fun getPendingReward(stageId: ProgressionStageId): PendingRewardRecord?
    fun getPendingRewards(): List<PendingRewardRecord>
    fun savePendingRewards(records: List<PendingRewardRecord>)
    fun removePendingRewards(stageIds: Collection<ProgressionStageId>)
    fun clearAllPendingRewards()
}
