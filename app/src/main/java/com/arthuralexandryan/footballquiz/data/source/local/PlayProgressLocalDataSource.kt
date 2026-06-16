package com.arthuralexandryan.footballquiz.data.source.local

import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.ScoreboardModel
import com.arthuralexandryan.footballquiz.utils.CategoryType

interface PlayProgressLocalDataSource {
    fun persistAnswer(
        questionId: String?,
        questionText: String,
        questionType: String,
        categoryType: CategoryType,
        placeScores: GameObjectScores,
        isCorrect: Boolean
    ): Int

    fun persistPlaceProgress(
        questionType: String,
        placeScores: GameObjectScores
    )

    fun resetPlaceProgress(
        questionType: String,
        categoryType: CategoryType,
        answeredScores: Int
    ): ScoreboardModel
}
