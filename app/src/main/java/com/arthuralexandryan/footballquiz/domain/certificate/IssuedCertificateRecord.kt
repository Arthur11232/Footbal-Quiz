package com.arthuralexandryan.footballquiz.domain.certificate

import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId

data class IssuedCertificateRecord(
    val stageId: ProgressionStageId,
    val certificateId: String,
    val issuedAtMillis: Long,
    val recipientName: String = ""
)
