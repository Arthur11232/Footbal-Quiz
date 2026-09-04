package com.arthuralexandryan.footballquiz.presentation.play

import com.arthuralexandryan.footballquiz.presentation.achievement.AchievementCertificatePayload

sealed interface PlayEffect {
    data class AnimateAnswer(
        val isCorrect: Boolean
    ) : PlayEffect

    data class ShowAchievementCertificate(
        val payload: AchievementCertificatePayload
    ) : PlayEffect

    data object ShowInterstitialAd : PlayEffect

    data object RequestAppReview : PlayEffect
}
