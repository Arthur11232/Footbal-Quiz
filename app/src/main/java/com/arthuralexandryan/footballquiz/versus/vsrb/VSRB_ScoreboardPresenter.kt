package com.arthuralexandryan.footballquiz.versus.vsrb

import android.content.Context
import com.arthuralexandryan.footballquiz.BuildConfig
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.data.repository.GameCatalogRepository
import com.arthuralexandryan.footballquiz.versus.AllCallBacks
import com.arthuralexandryan.footballquiz.versus.OnCheckScoreboard
import com.arthuralexandryan.footballquiz.versus.VS_ScoreboardModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class VSRB_ScoreboardPresenter(
    private val context: Context,
    private val checkScoreboard: OnCheckScoreboard
) : AllCallBacks.Presenter, KoinComponent {

    private val gameCatalogRepository: GameCatalogRepository by inject()

    init {
        getScoreboardScores()
    }

    fun getTotalScores(): Int = gameCatalogRepository.getTotalScore()
    fun getTotalScoresText(): String = gameCatalogRepository.getTotalScoreText()
    fun isReadyToPlay(): Boolean = BuildConfig.UNLOCK_ALL_STAGES || getTotalScores() >= Constant.FOR_RB_SCORE

    private fun getScoreboardScores() {
        val scoreboard = gameCatalogRepository.getRealBarcaScoreboard()
        checkScoreboard.onCheckScores(
            VS_ScoreboardModel(
                scoreboard.placeAnswered.toString(),
                scoreboard.placeScore.toString(),
                scoreboard.categoryScore.toString(),
                scoreboard.categoryAnswered.toString()
            )
        )
    }

    override fun onButtonClick() {}
}
