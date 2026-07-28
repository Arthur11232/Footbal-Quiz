package com.arthuralexandryan.footballquiz.domain.usecase.reward

import com.arthuralexandryan.footballquiz.data.repository.CertificateRepository
import com.arthuralexandryan.footballquiz.data.repository.PendingRewardRepository
import com.arthuralexandryan.footballquiz.domain.certificate.IssuedCertificateRecord
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.domain.reward.PendingRewardRecord
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RewardCertificateUseCasesTest {

    @Test
    fun `consume pending rewards converts guest rewards to issued certificates and uploads them`() = runBlocking {
        val pendingRepository = FakePendingRewardRepository(
            mutableListOf(
                PendingRewardRecord(
                    stageId = ProgressionStageId.TOP5_FRANCE_COMPLETE,
                    certificateId = "FQ-TOP5-FRANCE-123456",
                    earnedAtMillis = 123456L,
                    recipientNameSnapshot = "Guest",
                    createdAsGuest = true
                )
            )
        )
        val certificateRepository = FakeCertificateRepository()
        val useCase = ConsumePendingRewardsAfterAuthUseCase(pendingRepository, certificateRepository)

        val result = useCase(
            userId = "uid-1",
            recipientName = "Artur",
            userEmail = "arthur@example.com"
        )

        assertEquals(1, result.size)
        assertEquals(1, certificateRepository.localCertificates.size)
        assertEquals("Artur", certificateRepository.localCertificates.single().recipientName)
        assertEquals(1, certificateRepository.uploadCalls.size)
        assertTrue(pendingRepository.storedPendingRewards.isEmpty())
    }

    @Test
    fun `consume pending rewards does not duplicate already issued certificate and clears pending marker`() = runBlocking {
        val existingIssued = IssuedCertificateRecord(
            stageId = ProgressionStageId.TOP5_FRANCE_COMPLETE,
            certificateId = "FQ-TOP5-FRANCE-123456",
            issuedAtMillis = 123456L,
            recipientName = "Artur"
        )
        val pendingRepository = FakePendingRewardRepository(
            mutableListOf(
                PendingRewardRecord(
                    stageId = ProgressionStageId.TOP5_FRANCE_COMPLETE,
                    certificateId = "FQ-TOP5-FRANCE-123456",
                    earnedAtMillis = 123456L,
                    recipientNameSnapshot = "Guest",
                    createdAsGuest = true
                )
            )
        )
        val certificateRepository = FakeCertificateRepository(
            localCertificates = mutableListOf(existingIssued)
        )
        val useCase = ConsumePendingRewardsAfterAuthUseCase(pendingRepository, certificateRepository)

        val result = useCase(
            userId = "uid-1",
            recipientName = "Artur",
            userEmail = "arthur@example.com"
        )

        assertTrue(result.isEmpty())
        assertEquals(1, certificateRepository.localCertificates.size)
        assertTrue(certificateRepository.uploadCalls.isEmpty())
        assertNull(pendingRepository.getPendingReward(ProgressionStageId.TOP5_FRANCE_COMPLETE))
    }

    @Test
    fun `sync issued certificates uploads all local certificates in stable order`() = runBlocking {
        val certificateRepository = FakeCertificateRepository(
            localCertificates = mutableListOf(
                IssuedCertificateRecord(
                    stageId = ProgressionStageId.TOP5_COMPLETE,
                    certificateId = "FQ-TOP5-COMPLETE-999999",
                    issuedAtMillis = 999999L,
                    recipientName = "Artur"
                ),
                IssuedCertificateRecord(
                    stageId = ProgressionStageId.TOP5_FRANCE_COMPLETE,
                    certificateId = "FQ-TOP5-FRANCE-123456",
                    issuedAtMillis = 123456L,
                    recipientName = "Artur"
                )
            )
        )
        val useCase = SyncIssuedCertificatesToCloudUseCase(certificateRepository)

        val result = useCase(
            userId = "uid-1",
            recipientName = "Artur",
            userEmail = "arthur@example.com"
        )

        assertTrue(result.isSuccess)
        assertEquals(1, certificateRepository.uploadCalls.size)
        assertEquals(
            listOf(
                ProgressionStageId.TOP5_FRANCE_COMPLETE,
                ProgressionStageId.TOP5_COMPLETE
            ),
            certificateRepository.uploadCalls.single().records.map { it.stageId }
        )
    }

    private class FakePendingRewardRepository(
        val storedPendingRewards: MutableList<PendingRewardRecord> = mutableListOf()
    ) : PendingRewardRepository {

        override fun getPendingReward(stageId: ProgressionStageId): PendingRewardRecord? =
            storedPendingRewards.firstOrNull { it.stageId == stageId }

        override fun getPendingRewards(): List<PendingRewardRecord> = storedPendingRewards.toList()

        override fun savePendingRewards(records: List<PendingRewardRecord>) {
            records.forEach { record ->
                storedPendingRewards.removeAll { it.stageId == record.stageId }
                storedPendingRewards.add(record)
            }
        }

        override fun removePendingRewards(stageIds: Collection<ProgressionStageId>) {
            storedPendingRewards.removeAll { it.stageId in stageIds }
        }

        override fun clearAllPendingRewards() {
            storedPendingRewards.clear()
        }
    }

    private class FakeCertificateRepository(
        val localCertificates: MutableList<IssuedCertificateRecord> = mutableListOf()
    ) : CertificateRepository {

        val uploadCalls = mutableListOf<UploadCall>()

        override fun getIssuedCertificate(stageId: ProgressionStageId): IssuedCertificateRecord? =
            localCertificates.firstOrNull { it.stageId == stageId }

        override fun getIssuedCertificates(): List<IssuedCertificateRecord> = localCertificates.toList()

        override fun saveIssuedCertificates(records: List<IssuedCertificateRecord>) {
            records.forEach { record ->
                localCertificates.removeAll { it.stageId == record.stageId }
                localCertificates.add(record)
            }
        }

        override fun clearLocalIssuedCertificates() {
            localCertificates.clear()
        }

        override suspend fun uploadIssuedCertificates(
            userId: String,
            recipientName: String,
            userEmail: String?,
            records: List<IssuedCertificateRecord>
        ): Result<Unit> {
            uploadCalls += UploadCall(userId, recipientName, userEmail, records)
            return Result.success(Unit)
        }

        override suspend fun downloadIssuedCertificates(userId: String): List<IssuedCertificateRecord> = emptyList()

        override suspend fun deleteIssuedCertificates(userId: String): Result<Unit> = Result.success(Unit)
    }

    private data class UploadCall(
        val userId: String,
        val recipientName: String,
        val userEmail: String?,
        val records: List<IssuedCertificateRecord>
    )
}
