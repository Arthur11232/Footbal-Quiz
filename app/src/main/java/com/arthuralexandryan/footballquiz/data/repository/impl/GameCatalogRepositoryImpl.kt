package com.arthuralexandryan.footballquiz.data.repository.impl

import com.arthuralexandryan.footballquiz.data.repository.GameCatalogRepository
import com.arthuralexandryan.footballquiz.data.source.local.GameCatalogLocalDataSource
import com.arthuralexandryan.footballquiz.data.source.local.GameLaunchData
import com.arthuralexandryan.footballquiz.data.source.local.GameStateLocalDataSource
import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.ScoreboardModel

class GameCatalogRepositoryImpl(
    private val gameCatalogLocalDataSource: GameCatalogLocalDataSource,
    private val gameStateLocalDataSource: GameStateLocalDataSource
) : GameCatalogRepository {

    override fun getTop5Answered(): Int = gameCatalogLocalDataSource.getTop5Answered()

    override fun getTop5AndUefaAnswered(): Int = gameCatalogLocalDataSource.getTop5AndUefaAnswered()

    override fun getTotalScore(): Int = gameStateLocalDataSource.getProgressSnapshot().totalScore

    override fun getTotalScoreText(): String = getTotalScore().toString()

    override fun getTop5Scoreboards(): List<ScoreboardModel> = gameCatalogLocalDataSource.getTop5Scoreboards()

    override fun getUefaScoreboards(): List<ScoreboardModel> = gameCatalogLocalDataSource.getUefaScoreboards()

    override fun getWorldScoreboards(): List<ScoreboardModel> = gameCatalogLocalDataSource.getWorldScoreboards()

    override fun getRonMessiScoreboard(): ScoreboardModel = gameCatalogLocalDataSource.getRonMessiScoreboard()

    override fun getRealBarcaScoreboard(): ScoreboardModel = gameCatalogLocalDataSource.getRealBarcaScoreboard()

    override fun getCategoryScore(categoryKey: String): GameObjectScores = gameCatalogLocalDataSource.getCategoryScore(categoryKey)

    override fun getLaunchData(entryKey: String): GameLaunchData = gameCatalogLocalDataSource.getLaunchData(entryKey)
}
