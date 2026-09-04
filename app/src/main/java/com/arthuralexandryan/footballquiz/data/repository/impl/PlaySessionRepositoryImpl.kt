package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import com.arthuralexandryan.footballquiz.data.repository.PlaySessionRepository
import com.arthuralexandryan.footballquiz.data.repository.SyncRepository
import com.arthuralexandryan.footballquiz.data.source.local.GameStateLocalDataSource
import com.arthuralexandryan.footballquiz.data.source.local.PlayProgressLocalDataSource
import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser
import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.models.ScoreboardModel
import com.arthuralexandryan.footballquiz.utils.Prefer
import com.arthuralexandryan.footballquiz.utils.CategoryType

class PlaySessionRepositoryImpl(
    private val context: Context,
    private val playProgressLocalDataSource: PlayProgressLocalDataSource,
    private val gameStateLocalDataSource: GameStateLocalDataSource,
    private val syncRepository: SyncRepository
) : PlaySessionRepository {

    override fun consumeSavedQuestionIndex(categoryKey: String, placeType: String): Int? {
        val key = buildSavedQuestionStateKey(categoryKey, placeType)
        val savedState = Prefer.getIntPreference(context, key, -1)
        Prefer.setIntPreference(context, key, -1)
        return savedState.takeIf { it >= 0 }
    }

    override fun saveCurrentQuestionIndex(categoryKey: String, placeType: String, index: Int) {
        Prefer.setIntPreference(context, buildSavedQuestionStateKey(categoryKey, placeType), index)
    }

    override fun clearSavedQuestionIndex(categoryKey: String, placeType: String) {
        Prefer.setIntPreference(context, buildSavedQuestionStateKey(categoryKey, placeType), -1)
    }

    override fun persistAnswer(
        question: GameObjectSerializable,
        placeScores: GameObjectScores,
        isCorrect: Boolean
    ): Int {
        val questionId = question.questionId
        val questionText = question.question.orEmpty()
        val questionType = question.type.orEmpty()
        val categoryType = question.categoryType ?: return gameStateLocalDataSource.getProgressSnapshot().totalScore
        return playProgressLocalDataSource.persistAnswer(
            questionId = questionId,
            questionText = questionText,
            questionType = questionType,
            categoryType = categoryType,
            placeScores = placeScores,
            isCorrect = isCorrect
        )
    }

    override fun resetSession(
        questionType: String,
        categoryType: CategoryType,
        answeredCount: Int
    ): ScoreboardModel {
        return playProgressLocalDataSource.resetPlaceProgress(
            questionType = questionType,
            categoryType = categoryType,
            answeredScores = answeredCount
        )
    }

    override fun persistPlaceProgress(questionType: String, placeScores: GameObjectScores) {
        playProgressLocalDataSource.persistPlaceProgress(questionType, placeScores)
    }

    override fun getTotalScoreText(): String {
        return gameStateLocalDataSource.getProgressSnapshot().totalScore.toString()
    }

    override suspend fun syncProgressIfSignedIn(user: AuthenticatedUser): Result<Unit> {
        return syncRepository.uploadLocalStats(user)
    }

    private fun buildSavedQuestionStateKey(categoryKey: String, placeType: String): String {
        return "save_question_state_${categoryKey.ifBlank { "unknown" }}_${placeType.ifBlank { "unknown" }}"
    }
}
