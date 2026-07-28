package com.arthuralexandryan.footballquiz.versus.vsrm

import android.content.Context
import android.util.Log
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.data.repository.GameCatalogRepository
import com.arthuralexandryan.footballquiz.versus.AllCallBacks
import com.arthuralexandryan.footballquiz.versus.OnCheckScoreboard
import com.arthuralexandryan.footballquiz.versus.VS_ScoreboardModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VSRM_ScoreboardPresenter(val context: Context, private val checkScoreboard: OnCheckScoreboard) : AllCallBacks.Presenter, KoinComponent {
    private val gameCatalogRepository: GameCatalogRepository by inject()

    override fun onButtonClick() {
        toPlay()
    }

    init {
        getScoreboardScores()
    }


    fun getTotalScores(): Int {
        return gameCatalogRepository.getTotalScore()
    }

    fun getTotalScoresText(): String {
        return gameCatalogRepository.getTotalScoreText()
    }

    fun isReadyToPlay(): Boolean {
        return getTotalScores() >= Constant.FOR_RM_SCORE
    }
    private fun getScoreboardScores() {
        val scoreboard = gameCatalogRepository.getRonMessiScoreboard()
        checkScoreboard.onCheckScores(VS_ScoreboardModel(
                scoreboard.placeAnswered.toString(),
                scoreboard.placeScore.toString(),
                scoreboard.categoryScore.toString(),
                scoreboard.categoryAnswered.toString()))
    }

    private fun toPlay(){
        // Handled via CategoryPageFragment or should be handled via callback
        // For now, we'll just log it or use a callback if we want to support this legacy path
        Log.d("VSRM_Presenter", "toPlay requested. Should navigate via NavController.")
    }

}
