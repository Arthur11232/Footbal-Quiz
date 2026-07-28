package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import com.arthuralexandryan.footballquiz.data.repository.PendingRewardRepository
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.domain.reward.PendingRewardRecord
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer

class PendingRewardRepositoryImpl(
    private val context: Context
) : PendingRewardRepository {

    override fun getPendingReward(stageId: ProgressionStageId): PendingRewardRecord? {
        val earnedAtMillis = Prefer.getLongPreference(
            context,
            Constants.PendingRewardEarnedAtKeyPrefix + stageId.value,
            0L
        ).takeIf { it > 0L } ?: return null
        val certificateId = Prefer.getStringPreference(
            context,
            Constants.PendingRewardCertificateIdKeyPrefix + stageId.value,
            null
        )?.takeIf { it.isNotBlank() } ?: return null
        val recipientNameSnapshot = Prefer.getStringPreference(
            context,
            Constants.PendingRewardRecipientNameKeyPrefix + stageId.value,
            ""
        ).orEmpty()
        val createdAsGuest = Prefer.getBooleanPreference(
            context,
            Constants.PendingRewardCreatedAsGuestKeyPrefix + stageId.value,
            true
        )
        return PendingRewardRecord(
            stageId = stageId,
            certificateId = certificateId,
            earnedAtMillis = earnedAtMillis,
            recipientNameSnapshot = recipientNameSnapshot,
            createdAsGuest = createdAsGuest
        )
    }

    override fun getPendingRewards(): List<PendingRewardRecord> =
        ProgressionStageId.entries.mapNotNull(::getPendingReward)

    override fun savePendingRewards(records: List<PendingRewardRecord>) {
        val editor = Prefer.getSharedPreferenceEditor(context)
        records.forEach { record ->
            editor.putLong(
                Constants.PendingRewardEarnedAtKeyPrefix + record.stageId.value,
                record.earnedAtMillis
            )
            editor.putString(
                Constants.PendingRewardCertificateIdKeyPrefix + record.stageId.value,
                record.certificateId
            )
            editor.putString(
                Constants.PendingRewardRecipientNameKeyPrefix + record.stageId.value,
                record.recipientNameSnapshot
            )
            editor.putBoolean(
                Constants.PendingRewardCreatedAsGuestKeyPrefix + record.stageId.value,
                record.createdAsGuest
            )
        }
        editor.apply()
    }

    override fun removePendingRewards(stageIds: Collection<ProgressionStageId>) {
        if (stageIds.isEmpty()) return
        val editor = Prefer.getSharedPreferenceEditor(context)
        stageIds.forEach { stageId ->
            editor.remove(Constants.PendingRewardEarnedAtKeyPrefix + stageId.value)
            editor.remove(Constants.PendingRewardCertificateIdKeyPrefix + stageId.value)
            editor.remove(Constants.PendingRewardRecipientNameKeyPrefix + stageId.value)
            editor.remove(Constants.PendingRewardCreatedAsGuestKeyPrefix + stageId.value)
        }
        editor.apply()
    }

    override fun clearAllPendingRewards() {
        removePendingRewards(ProgressionStageId.entries)
    }
}
