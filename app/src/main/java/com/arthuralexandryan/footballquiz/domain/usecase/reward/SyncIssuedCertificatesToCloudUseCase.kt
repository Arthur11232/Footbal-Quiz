package com.arthuralexandryan.footballquiz.domain.usecase.reward

import com.arthuralexandryan.footballquiz.data.repository.CertificateRepository
import com.arthuralexandryan.footballquiz.domain.certificate.IssuedCertificateRecord

class SyncIssuedCertificatesToCloudUseCase(
    private val certificateRepository: CertificateRepository
) {
    suspend operator fun invoke(
        userId: String,
        recipientName: String,
        userEmail: String?
    ): Result<Unit> {
        val localCertificates = certificateRepository.getIssuedCertificates()
            .sortedBy(IssuedCertificateRecord::issuedAtMillis)
        if (localCertificates.isEmpty()) {
            return Result.success(Unit)
        }

        return certificateRepository.uploadIssuedCertificates(
            userId = userId,
            recipientName = recipientName,
            userEmail = userEmail,
            records = localCertificates
        )
    }
}
