package com.arthuralexandryan.footballquiz.presentation.play

import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable

data class PlaySessionArgs(
    val questions: List<GameObjectSerializable>,
    val placeScores: GameObjectScores,
    val categoryScores: GameObjectScores,
    val titleText: String,
    val categoryKey: String
)
