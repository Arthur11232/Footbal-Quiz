package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import com.arthuralexandryan.footballquiz.data.repository.CertificateRepository
import com.arthuralexandryan.footballquiz.domain.certificate.IssuedCertificateRecord
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.models.UserCertificateDTO
import com.arthuralexandryan.footballquiz.models.toIssuedCertificateRecord
import com.arthuralexandryan.footballquiz.models.toUserCertificateDTO
import com.arthuralexandryan.footballquiz.models.UserStatsService
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CertificateRepositoryImpl(
    private val context: Context,
    private val userStatsService: UserStatsService
) : CertificateRepository {

    override fun getIssuedCertificate(stageId: ProgressionStageId): IssuedCertificateRecord? {
        val issuedAt = Prefer.getLongPreference(
            context,
            Constants.CertificateIssuedAtKeyPrefix + stageId.value,
            0L
        ).takeIf { it > 0L } ?: return null
        val certificateId = Prefer.getStringPreference(
            context,
            Constants.CertificateIdKeyPrefix + stageId.value,
            null
        )?.takeIf { it.isNotBlank() } ?: return null
        val recipientName = Prefer.getStringPreference(
            context,
            Constants.CertificateRecipientNameKeyPrefix + stageId.value,
            ""
        ).orEmpty()
        return IssuedCertificateRecord(
            stageId = stageId,
            certificateId = certificateId,
            issuedAtMillis = issuedAt,
            recipientName = recipientName
        )
    }

    override fun getIssuedCertificates(): List<IssuedCertificateRecord> {
        return ProgressionStageId.entries.mapNotNull(::getIssuedCertificate)
    }

    override fun saveIssuedCertificates(records: List<IssuedCertificateRecord>) {
        val editor = Prefer.getSharedPreferenceEditor(context)
        records.forEach { record ->
            editor.putLong(
                Constants.CertificateIssuedAtKeyPrefix + record.stageId.value,
                record.issuedAtMillis
            )
            editor.putString(
                Constants.CertificateIdKeyPrefix + record.stageId.value,
                record.certificateId
            )
            editor.putString(
                Constants.CertificateRecipientNameKeyPrefix + record.stageId.value,
                record.recipientName
            )
        }
        editor.apply()
    }

    override fun clearLocalIssuedCertificates() {
        val editor = Prefer.getSharedPreferenceEditor(context)
        ProgressionStageId.entries.forEach { stageId ->
            editor.remove(Constants.CertificateIssuedAtKeyPrefix + stageId.value)
            editor.remove(Constants.CertificateIdKeyPrefix + stageId.value)
            editor.remove(Constants.CertificateRecipientNameKeyPrefix + stageId.value)
        }
        editor.apply()
    }

    override suspend fun uploadIssuedCertificates(
        userId: String,
        recipientName: String,
        userEmail: String?,
        records: List<IssuedCertificateRecord>
    ): Result<Unit> {
        if (records.isEmpty()) return Result.success(Unit)
        val payload = records.map { record ->
            val resolvedRecipientName = record.recipientName.ifBlank { recipientName }
            record.copy(recipientName = resolvedRecipientName).toUserCertificateDTO(userEmail)
        }
        return suspendCancellableCoroutine { continuation ->
            userStatsService.uploadCertificates(userId, payload) { success ->
                if (success) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(
                        Result.failure(IllegalStateException("Failed to upload cloud certificates"))
                    )
                }
            }
        }
    }

    override suspend fun downloadIssuedCertificates(userId: String): List<IssuedCertificateRecord> {
        return suspendCancellableCoroutine { continuation ->
            userStatsService.downloadCertificates(userId) { certificates ->
                continuation.resume(
                    certificates.mapNotNull(UserCertificateDTO::toIssuedCertificateRecord)
                )
            }
        }
    }

    override suspend fun deleteIssuedCertificates(userId: String): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            userStatsService.deleteCertificates(userId) { success ->
                if (success) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(
                        Result.failure(IllegalStateException("Failed to delete cloud certificates"))
                    )
                }
            }
        }
    }
}
