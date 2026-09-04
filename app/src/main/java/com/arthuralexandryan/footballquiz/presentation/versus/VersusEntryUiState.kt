package com.arthuralexandryan.footballquiz.presentation.versus

data class VersusEntryUiState(
    val isLoaded: Boolean = false,
    val isReady: Boolean = false,
    val totalScore: Int = 0,
    val totalScoreText: String = "0",
    val unlockThreshold: Int = 0,
    val placeAnswerText: String = "0",
    val placeQuestionText: String = "0",
    val categoryScoreText: String = "0",
    val categoryAnswerText: String = "0"
)
