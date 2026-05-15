package com.arthuralexandryan.footballquiz.data.repository

import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.google.firebase.auth.FirebaseUser

interface PlaySessionRepository {
    fun consumeSavedQuestionIndex(categoryKey: String, placeType: String): Int?
    fun saveCurrentQuestionIndex(categoryKey: String, placeType: String, index: Int)
    fun clearSavedQuestionIndex(categoryKey: String, placeType: String)
    fun persistAnswer(
        question: GameObjectSerializable,
        placeScores: GameObjectScores,
        isCorrect: Boolean
    ): Int
    fun persistPlaceProgress(questionType: String, placeScores: GameObjectScores)
    fun getTotalScoreText(): String
    suspend fun syncProgressIfSignedIn(user: FirebaseUser): Result<Unit>
}
