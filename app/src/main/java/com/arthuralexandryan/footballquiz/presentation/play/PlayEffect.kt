package com.arthuralexandryan.footballquiz.presentation.play

sealed interface PlayEffect {
    data class AnimateAnswer(
        val isCorrect: Boolean
    ) : PlayEffect

    data object ShowInterstitialAd : PlayEffect
}
