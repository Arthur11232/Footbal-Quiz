package com.arthuralexandryan.footballquiz.presentation.versus

import androidx.lifecycle.ViewModel
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.data.repository.GameCatalogRepository
import com.arthuralexandryan.footballquiz.data.source.local.GameLaunchData
import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.ScoreboardModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VersusEntryViewModel(
    private val gameCatalogRepository: GameCatalogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VersusEntryUiState())
    val uiState: StateFlow<VersusEntryUiState> = _uiState.asStateFlow()

    fun load(entryKey: String) {
        val threshold = entryKey.unlockThreshold()
        val totalScore = gameCatalogRepository.getTotalScore()
        val scoreboard = entryKey.scoreboard()

        _uiState.value = VersusEntryUiState(
            isLoaded = true,
            isReady = totalScore >= threshold,
            totalScore = totalScore,
            totalScoreText = gameCatalogRepository.getTotalScoreText(),
            unlockThreshold = threshold,
            placeAnswerText = scoreboard.placeAnswered.toString(),
            placeQuestionText = scoreboard.placeScore.toString(),
            categoryScoreText = scoreboard.categoryScore.toString(),
            categoryAnswerText = scoreboard.categoryAnswered.toString()
        )
    }

    fun getLaunchData(entryKey: String): GameLaunchData {
        return gameCatalogRepository.getLaunchData(entryKey)
    }

    fun getCategoryScore(entryKey: String): GameObjectScores {
        return gameCatalogRepository.getCategoryScore(entryKey)
    }

    private fun String.unlockThreshold(): Int {
        return when (this) {
            ENTRY_RONALDO_MESSI -> Constant.FOR_RM_SCORE
            ENTRY_REAL_BARCELONA -> Constant.FOR_RB_SCORE
            else -> 0
        }
    }

    private fun String.scoreboard(): ScoreboardModel {
        return when (this) {
            ENTRY_RONALDO_MESSI -> gameCatalogRepository.getRonMessiScoreboard()
            ENTRY_REAL_BARCELONA -> gameCatalogRepository.getRealBarcaScoreboard()
            else -> ScoreboardModel(0, 0, 0, 0)
        }
    }

    companion object {
        const val ENTRY_RONALDO_MESSI = "vsRM"
        const val ENTRY_REAL_BARCELONA = "vsRB"
    }
}
