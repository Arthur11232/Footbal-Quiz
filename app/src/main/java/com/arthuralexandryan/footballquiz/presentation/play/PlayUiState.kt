package com.arthuralexandryan.footballquiz.presentation.play

data class PlayAnswerOption(
    val id: String,
    val text: String
)

data class PlayUiState(
    val isInitialized: Boolean = false,
    val isAnswerEnabled: Boolean = false,
    val titleText: String = "",
    val questionText: String = "",
    val questionNumberText: String = "",
    val placeQuestionText: String = "",
    val placeAnswerText: String = "",
    val categoryAnswersText: String = "",
    val categoryScoreText: String = "",
    val answerOptions: List<PlayAnswerOption> = emptyList(),
    val showFinishScreen: Boolean = false,
    val totalGoalText: String = "0",
    val isResetEnabled: Boolean = false,
    val forceResetAlpha: Float = 0.2f,
    val answerFeedbackResId: Int? = null,
    val isAnswerCorrect: Boolean? = null
)
