package com.arthuralexandryan.footballquiz.data.repository

import com.arthuralexandryan.footballquiz.domain.certificate.IssuedCertificateRecord
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId

interface CertificateRepository {
    fun getIssuedCertificate(stageId: ProgressionStageId): IssuedCertificateRecord?
    fun getIssuedCertificates(): List<IssuedCertificateRecord>
    fun saveIssuedCertificates(records: List<IssuedCertificateRecord>)
    fun clearLocalIssuedCertificates()
    suspend fun uploadIssuedCertificates(
        userId: String,
        recipientName: String,
        userEmail: String?,
        records: List<IssuedCertificateRecord>
    ): Result<Unit>
    suspend fun downloadIssuedCertificates(userId: String): List<IssuedCertificateRecord>
    suspend fun deleteIssuedCertificates(userId: String): Result<Unit>
}
