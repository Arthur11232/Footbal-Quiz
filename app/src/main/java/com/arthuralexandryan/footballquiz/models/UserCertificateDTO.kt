package com.arthuralexandryan.footballquiz.models

import com.arthuralexandryan.footballquiz.BuildConfig
import com.arthuralexandryan.footballquiz.domain.certificate.IssuedCertificateRecord
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId

data class UserCertificateDTO(
    var stageId: String = "",
    var certificateId: String = "",
    var issuedAt: Long = 0L,
    var recipientName: String = "",
    var email: String = "",
    var appVersion: String = "",
    var source: String = SOURCE_ANDROID
) {
    companion object {
        const val SOURCE_ANDROID = "android"
    }
}

fun IssuedCertificateRecord.toUserCertificateDTO(email: String?): UserCertificateDTO {
    return UserCertificateDTO(
        stageId = stageId.value,
        certificateId = certificateId,
        issuedAt = issuedAtMillis,
        recipientName = recipientName,
        email = email.orEmpty(),
        appVersion = BuildConfig.VERSION_NAME,
        source = UserCertificateDTO.SOURCE_ANDROID
    )
}

fun UserCertificateDTO.toIssuedCertificateRecord(): IssuedCertificateRecord? {
    val resolvedStageId = ProgressionStageId.fromValue(stageId) ?: return null
    val resolvedIssuedAt = issuedAt.takeIf { it > 0L } ?: return null
    if (certificateId.isBlank()) return null
    return IssuedCertificateRecord(
        stageId = resolvedStageId,
        certificateId = certificateId,
        issuedAtMillis = resolvedIssuedAt,
        recipientName = recipientName
    )
}
