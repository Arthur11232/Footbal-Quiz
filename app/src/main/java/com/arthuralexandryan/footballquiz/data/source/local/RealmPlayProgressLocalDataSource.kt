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
import com.arthuralexandryan.footballquiz.models.ScoreboardModel
import com.arthuralexandryan.footballquiz.utils.CategoryType
import com.arthuralexandryan.footballquiz.utils.getOpenedPlace
import io.realm.Realm

class RealmPlayProgressLocalDataSource(
    private val context: Context,
    private val gameStateLocalDataSource: GameStateLocalDataSource
) : PlayProgressLocalDataSource {

    override fun persistAnswer(
        questionId: String?,
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
                    "France" -> findQuestionByIdentity(transactionRealm, DB_France::class.java, questionId, questionText)?.isAnswered = true
                    "Germany" -> findQuestionByIdentity(transactionRealm, DB_Germany::class.java, questionId, questionText)?.isAnswered = true
                    "Italy" -> findQuestionByIdentity(transactionRealm, DB_Italy::class.java, questionId, questionText)?.isAnswered = true
                    "England" -> findQuestionByIdentity(transactionRealm, DB_England::class.java, questionId, questionText)?.isAnswered = true
                    "Spain" -> findQuestionByIdentity(transactionRealm, DB_Spain::class.java, questionId, questionText)?.isAnswered = true
                    "Super Cup" -> findQuestionByIdentity(transactionRealm, DB_Super_Cup::class.java, questionId, questionText)?.isAnswered = true
                    "European League" -> findQuestionByIdentity(transactionRealm, DB_Europa_League::class.java, questionId, questionText)?.isAnswered = true
                    "Champions League" -> findQuestionByIdentity(transactionRealm, DB_Champions_League::class.java, questionId, questionText)?.isAnswered = true
                    "Europe Championship" -> findQuestionByIdentity(transactionRealm, DB_Europ_Championship::class.java, questionId, questionText)?.isAnswered = true
                    "World Cup" -> findQuestionByIdentity(transactionRealm, DB_World_Championship::class.java, questionId, questionText)?.isAnswered = true
                    "Versus R_M" -> findQuestionByIdentity(transactionRealm, DB_VS_Ronaldo_Messi::class.java, questionId, questionText)?.isAnswered = true
                    "Versus R_B" -> findQuestionByIdentity(transactionRealm, DB_VS_RealM_Barcelona::class.java, questionId, questionText)?.isAnswered = true
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

    override fun resetPlaceProgress(
        questionType: String,
        categoryType: CategoryType,
        answeredScores: Int
    ): ScoreboardModel {
        return Realm.getDefaultInstance().use { realm ->
            var resetScoreboard = defaultResetScoreboard(categoryType)
            realm.executeTransaction { transactionRealm ->
                disableAllAnswers(transactionRealm, questionType)
                val scores = transactionRealm.where(FQ_Scores::class.java).findFirst() ?: return@executeTransaction

                resetScoreboard = when (questionType) {
                    "France" -> {
                        scores.top5Scores?.france_score = 40
                        scores.top5Scores?.france_answered = 0
                        scores.top5_answered = (scores.top5_answered - answeredScores).coerceAtLeast(0)
                        ScoreboardModel(40, 0, scores.top5_answered, scores.top5)
                    }
                    "Germany" -> {
                        scores.top5Scores?.germany_score = 40
                        scores.top5Scores?.germany_answered = 0
                        scores.top5_answered = (scores.top5_answered - answeredScores).coerceAtLeast(0)
                        ScoreboardModel(40, 0, scores.top5_answered, scores.top5)
                    }
                    "Italy" -> {
                        scores.top5Scores?.italy_score = 40
                        scores.top5Scores?.italy_answered = 0
                        scores.top5_answered = (scores.top5_answered - answeredScores).coerceAtLeast(0)
                        ScoreboardModel(40, 0, scores.top5_answered, scores.top5)
                    }
                    "England" -> {
                        scores.top5Scores?.england_score = 40
                        scores.top5Scores?.england_answered = 0
                        scores.top5_answered = (scores.top5_answered - answeredScores).coerceAtLeast(0)
                        ScoreboardModel(40, 0, scores.top5_answered, scores.top5)
                    }
                    "Spain" -> {
                        scores.top5Scores?.spain_score = 40
                        scores.top5Scores?.spain_answered = 0
                        scores.top5_answered = (scores.top5_answered - answeredScores).coerceAtLeast(0)
                        ScoreboardModel(40, 0, scores.top5_answered, scores.top5)
                    }
                    "Super Cup" -> {
                        scores.ufaScores?.superCup_score = 20
                        scores.ufaScores?.superCup_answered = 0
                        scores.ufa_answered = (scores.ufa_answered - answeredScores).coerceAtLeast(0)
                        ScoreboardModel(20, 0, scores.ufa_answered, scores.ufa)
                    }
                    "European League" -> {
                        scores.ufaScores?.europaLeague_score = 20
                        scores.ufaScores?.europaLeague_answered = 0
                        scores.ufa_answered = (scores.ufa_answered - answeredScores).coerceAtLeast(0)
                        ScoreboardModel(20, 0, scores.ufa_answered, scores.ufa)
                    }
                    "Champions League" -> {
                        scores.ufaScores?.championsLeague_score = 20
                        scores.ufaScores?.championsLeague_answered = 0
                        scores.ufa_answered = (scores.ufa_answered - answeredScores).coerceAtLeast(0)
                        ScoreboardModel(20, 0, scores.ufa_answered, scores.ufa)
                    }
                    "Europe Championship" -> {
                        scores.ufaScores?.europaChampionship_score = 10
                        scores.ufaScores?.europaChampionship_answered = 0
                        scores.ufa_answered = (scores.ufa_answered - answeredScores).coerceAtLeast(0)
                        ScoreboardModel(10, 0, scores.ufa_answered, scores.ufa)
                    }
                    "World Cup" -> {
                        scores.world = 30
                        scores.world_answered = 0
                        ScoreboardModel(30, 0, scores.world_answered, scores.world)
                    }
                    "Versus R_M" -> {
                        scores.vsRM = 50
                        scores.vsRM_answered = 0
                        ScoreboardModel(50, 0, scores.vsRM_answered, scores.vsRM)
                    }
                    "Versus R_B" -> {
                        scores.vsRB = 50
                        scores.vsRB_answered = 0
                        ScoreboardModel(50, 0, scores.vsRB_answered, scores.vsRB)
                    }
                    else -> resetScoreboard
                }

                transactionRealm.insertOrUpdate(scores)
            }
            resetScoreboard
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

    private fun defaultResetScoreboard(categoryType: CategoryType): ScoreboardModel {
        return when (categoryType) {
            CategoryType.TOP5 -> ScoreboardModel(40, 0, 0, 200)
            CategoryType.UEFA -> ScoreboardModel(20, 0, 0, 70)
            CategoryType.WORLD_CUP -> ScoreboardModel(30, 0, 0, 30)
            CategoryType.RON_MES,
            CategoryType.REAL_BARCE -> ScoreboardModel(50, 0, 0, 50)
        }
    }

    private fun disableAllAnswers(realm: Realm, questionType: String) {
        when (questionType) {
            "France" -> realm.where(DB_France::class.java).findAll().forEach { it.isAnswered = false }
            "Germany" -> realm.where(DB_Germany::class.java).findAll().forEach { it.isAnswered = false }
            "Italy" -> realm.where(DB_Italy::class.java).findAll().forEach { it.isAnswered = false }
            "England" -> realm.where(DB_England::class.java).findAll().forEach { it.isAnswered = false }
            "Spain" -> realm.where(DB_Spain::class.java).findAll().forEach { it.isAnswered = false }
            "Super Cup" -> realm.where(DB_Super_Cup::class.java).findAll().forEach { it.isAnswered = false }
            "European League" -> realm.where(DB_Europa_League::class.java).findAll().forEach { it.isAnswered = false }
            "Champions League" -> realm.where(DB_Champions_League::class.java).findAll().forEach { it.isAnswered = false }
            "Europe Championship" -> realm.where(DB_Europ_Championship::class.java).findAll().forEach { it.isAnswered = false }
            "World Cup" -> realm.where(DB_World_Championship::class.java).findAll().forEach { it.isAnswered = false }
            "Versus R_M" -> realm.where(DB_VS_Ronaldo_Messi::class.java).findAll().forEach { it.isAnswered = false }
            "Versus R_B" -> realm.where(DB_VS_RealM_Barcelona::class.java).findAll().forEach { it.isAnswered = false }
        }
    }

    private fun <T> findQuestionByIdentity(
        realm: Realm,
        modelClass: Class<T>,
        questionId: String?,
        questionText: String
    ): T? where T : io.realm.RealmModel, T : com.arthuralexandryan.footballquiz.db_app.DB_Base {
        return if (!questionId.isNullOrBlank()) {
            realm.where(modelClass).equalTo("questionId", questionId).findFirst()
                ?: realm.where(modelClass).equalTo("question", questionText).findFirst()
        } else {
            realm.where(modelClass).equalTo("question", questionText).findFirst()
        }
    }
}
