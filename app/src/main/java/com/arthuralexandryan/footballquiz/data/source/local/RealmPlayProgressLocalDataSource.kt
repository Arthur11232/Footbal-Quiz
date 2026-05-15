package com.arthuralexandryan.footballquiz.data.source.local

import android.content.Context
import com.arthuralexandryan.footballquiz.db_app.Score.FQ_Scores
import com.arthuralexandryan.footballquiz.db_app.Top5.DB_England
import com.arthuralexandryan.footballquiz.db_app.Top5.DB_France
import com.arthuralexandryan.footballquiz.db_app.Top5.DB_Germany
import com.arthuralexandryan.footballquiz.db_app.Top5.DB_Italy
import com.arthuralexandryan.footballquiz.db_app.Top5.DB_Spain
import com.arthuralexandryan.footballquiz.db_app.UFA.DB_Champions_League
import com.arthuralexandryan.footballquiz.db_app.UFA.DB_Europ_Championship
import com.arthuralexandryan.footballquiz.db_app.UFA.DB_Europa_League
import com.arthuralexandryan.footballquiz.db_app.UFA.DB_Super_Cup
import com.arthuralexandryan.footballquiz.db_app.Versus.DB_VS_RealM_Barcelona
import com.arthuralexandryan.footballquiz.db_app.Versus.DB_VS_Ronaldo_Messi
import com.arthuralexandryan.footballquiz.db_app.World.DB_World_Championship
import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.utils.CategoryType
import com.arthuralexandryan.footballquiz.utils.getOpenedPlace
import io.realm.Realm

class RealmPlayProgressLocalDataSource(
    private val context: Context,
    private val gameStateLocalDataSource: GameStateLocalDataSource
) : PlayProgressLocalDataSource {

    override fun persistAnswer(
        questionText: String,
        questionType: String,
        categoryType: CategoryType,
        placeScores: GameObjectScores,
        isCorrect: Boolean
    ): Int {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction { transactionRealm ->
                val scores = transactionRealm.where(FQ_Scores::class.java).findFirst() ?: return@executeTransaction

                when (questionType) {
                    "France" -> transactionRealm.where(DB_France::class.java).equalTo("question", questionText).findFirst()?.isAnswered = true
                    "Germany" -> transactionRealm.where(DB_Germany::class.java).equalTo("question", questionText).findFirst()?.isAnswered = true
                    "Italy" -> transactionRealm.where(DB_Italy::class.java).equalTo("question", questionText).findFirst()?.isAnswered = true
                    "England" -> transactionRealm.where(DB_England::class.java).equalTo("question", questionText).findFirst()?.isAnswered = true
                    "Spain" -> transactionRealm.where(DB_Spain::class.java).equalTo("question", questionText).findFirst()?.isAnswered = true
                    "Super Cup" -> transactionRealm.where(DB_Super_Cup::class.java).equalTo("question", questionText).findFirst()?.isAnswered = true
                    "European League" -> transactionRealm.where(DB_Europa_League::class.java).equalTo("question", questionText).findFirst()?.isAnswered = true
                    "Champions League" -> transactionRealm.where(DB_Champions_League::class.java).equalTo("question", questionText).findFirst()?.isAnswered = true
                    "Europe Championship" -> transactionRealm.where(DB_Europ_Championship::class.java).equalTo("question", questionText).findFirst()?.isAnswered = true
                    "World Cup" -> transactionRealm.where(DB_World_Championship::class.java).equalTo("question", questionText).findFirst()?.isAnswered = true
                    "Versus R_M" -> transactionRealm.where(DB_VS_Ronaldo_Messi::class.java).equalTo("question", questionText).findFirst()?.isAnswered = true
                    "Versus R_B" -> transactionRealm.where(DB_VS_RealM_Barcelona::class.java).equalTo("question", questionText).findFirst()?.isAnswered = true
                }

                updatePlaceScores(scores, questionType, placeScores)
                if (isCorrect) {
                    incrementCategoryAnswered(scores, categoryType)
                    val totalScore = scores.top5_answered + scores.ufa_answered + scores.world_answered +
                        scores.vsRM_answered + scores.vsRB_answered
                    getOpenedPlace(totalScore, context)
                }
                transactionRealm.insertOrUpdate(scores)
            }
        }

        return gameStateLocalDataSource.getProgressSnapshot().totalScore
    }

    override fun persistPlaceProgress(questionType: String, placeScores: GameObjectScores) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction { transactionRealm ->
                val scores = transactionRealm.where(FQ_Scores::class.java).findFirst() ?: return@executeTransaction
                updatePlaceScores(scores, questionType, placeScores)
                transactionRealm.insertOrUpdate(scores)
            }
        }
    }

    private fun updatePlaceScores(scores: FQ_Scores, questionType: String, placeScores: GameObjectScores) {
        when (questionType) {
            "France" -> {
                scores.top5Scores?.france_answered = placeScores.place_score_answer
                scores.top5Scores?.france_score = placeScores.place_score
            }
            "Germany" -> {
                scores.top5Scores?.germany_answered = placeScores.place_score_answer
                scores.top5Scores?.germany_score = placeScores.place_score
            }
            "Italy" -> {
                scores.top5Scores?.italy_answered = placeScores.place_score_answer
                scores.top5Scores?.italy_score = placeScores.place_score
            }
            "England" -> {
                scores.top5Scores?.england_answered = placeScores.place_score_answer
                scores.top5Scores?.england_score = placeScores.place_score
            }
            "Spain" -> {
                scores.top5Scores?.spain_answered = placeScores.place_score_answer
                scores.top5Scores?.spain_score = placeScores.place_score
            }
            "Super Cup" -> {
                scores.ufaScores?.superCup_answered = placeScores.place_score_answer
                scores.ufaScores?.superCup_score = placeScores.place_score
            }
            "European League" -> {
                scores.ufaScores?.europaLeague_answered = placeScores.place_score_answer
                scores.ufaScores?.europaLeague_score = placeScores.place_score
            }
            "Champions League" -> {
                scores.ufaScores?.championsLeague_answered = placeScores.place_score_answer
                scores.ufaScores?.championsLeague_score = placeScores.place_score
            }
            "Europe Championship" -> {
                scores.ufaScores?.europaChampionship_answered = placeScores.place_score_answer
                scores.ufaScores?.europaChampionship_score = placeScores.place_score
            }
            "World Cup" -> {
                scores.world_answered = placeScores.place_score_answer
                scores.world = placeScores.place_score
            }
            "Versus R_M" -> {
                scores.vsRM_answered = placeScores.place_score_answer
                scores.vsRM = placeScores.place_score
            }
            "Versus R_B" -> {
                scores.vsRB_answered = placeScores.place_score_answer
                scores.vsRB = placeScores.place_score
            }
        }
    }

    private fun incrementCategoryAnswered(scores: FQ_Scores, categoryType: CategoryType) {
        when (categoryType) {
            CategoryType.TOP5 -> scores.top5_answered = scores.top5_answered + 1
            CategoryType.UEFA -> scores.ufa_answered = scores.ufa_answered + 1
            CategoryType.WORLD_CUP -> scores.world_answered = scores.world_answered + 1
            CategoryType.RON_MES -> scores.vsRM_answered = scores.vsRM_answered + 1
            CategoryType.REAL_BARCE -> scores.vsRB_answered = scores.vsRB_answered + 1
        }
    }
}
