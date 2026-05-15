package com.arthuralexandryan.footballquiz.data.repository

import com.arthuralexandryan.footballquiz.data.source.local.GameLaunchData
import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.ScoreboardModel

interface GameCatalogRepository {
    fun getTop5Answered(): Int
    fun getTop5AndUefaAnswered(): Int
    fun getTotalScore(): Int
    fun getTotalScoreText(): String
    fun getTop5Scoreboards(): List<ScoreboardModel>
    fun getUefaScoreboards(): List<ScoreboardModel>
    fun getWorldScoreboards(): List<ScoreboardModel>
    fun getRonMessiScoreboard(): ScoreboardModel
    fun getRealBarcaScoreboard(): ScoreboardModel
    fun getCategoryScore(categoryKey: String): GameObjectScores
    fun getLaunchData(entryKey: String): GameLaunchData
}
