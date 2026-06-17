package com.arthuralexandryan.footballquiz.data.source.local

import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.models.ScoreboardModel

data class GameLaunchData(
    val questions: List<GameObjectSerializable>,
    val placeScores: GameObjectScores
)

interface GameCatalogLocalDataSource {
    fun getTop5Answered(): Int
    fun getTop5AndUefaAnswered(): Int
    fun getTop5Scoreboards(): List<ScoreboardModel>
    fun getUefaScoreboards(): List<ScoreboardModel>
    fun getWorldScoreboards(): List<ScoreboardModel>
    fun getRonMessiScoreboard(): ScoreboardModel
    fun getRealBarcaScoreboard(): ScoreboardModel
    fun getCategoryScore(categoryKey: String): GameObjectScores
    fun getLaunchData(entryKey: String): GameLaunchData
}
