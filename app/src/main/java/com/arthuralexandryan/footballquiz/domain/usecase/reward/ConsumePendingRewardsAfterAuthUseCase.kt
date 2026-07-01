package com.arthuralexandryan.footballquiz.domain.usecase.reward

import com.arthuralexandryan.footballquiz.data.repository.CertificateRepository
import com.arthuralexandryan.footballquiz.data.repository.PendingRewardRepository
import com.arthuralexandryan.footballquiz.domain.certificate.IssuedCertificateRecord
import com.arthuralexandryan.footballquiz.domain.reward.PendingRewardRecord

class ConsumePendingRewardsAfterAuthUseCase(
    private val pendingRewardRepository: PendingRewardRepository,
    private val certificateRepository: CertificateRepository
) {
    suspend operator fun invoke(
        userId: String,
        recipientName: String,
        userEmail: String?
    ): List<IssuedCertificateRecord> {
        val pendingRewards = pendingRewardRepository.getPendingRewards()
            .sortedBy(PendingRewardRecord::earnedAtMillis)
        if (pendingRewards.isEmpty()) return emptyList()

        val consumedStageIds = pendingRewards.map { it.stageId }
        val convertedRecords = pendingRewards.mapNotNull { pendingReward ->
            if (certificateRepository.getIssuedCertificate(pendingReward.stageId) != null) {
                null
            } else {
                IssuedCertificateRecord(
                    stageId = pendingReward.stageId,
                    certificateId = pendingReward.certificateId,
                    issuedAtMillis = pendingReward.earnedAtMillis,
                    recipientName = recipientName
                )
            }
        }

        if (convertedRecords.isNotEmpty()) {
            certificateRepository.saveIssuedCertificates(convertedRecords)
        }
        pendingRewardRepository.removePendingRewards(consumedStageIds)

        if (convertedRecords.isNotEmpty()) {
            certificateRepository.uploadIssuedCertificates(
                userId = userId,
                recipientName = recipientName,
                userEmail = userEmail,
                records = convertedRecords
            )
        }

        return convertedRecords
    }
}
