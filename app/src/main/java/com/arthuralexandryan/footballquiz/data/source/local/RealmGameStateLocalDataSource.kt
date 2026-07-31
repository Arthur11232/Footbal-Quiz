package com.arthuralexandryan.footballquiz.data.source.local

import android.util.Log
import com.arthuralexandryan.footballquiz.constants.Constant.LOG_DB
import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser
import com.arthuralexandryan.footballquiz.db_app.DB_Base
import com.arthuralexandryan.footballquiz.db_app.Score.FQ_Scores
import com.arthuralexandryan.footballquiz.db_app.Score.Objects.PlacesTop5Obj
import com.arthuralexandryan.footballquiz.db_app.Score.Objects.PlacesUFAObj
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
import com.arthuralexandryan.footballquiz.models.GameState
import com.arthuralexandryan.footballquiz.models.PlacesTop5DTO
import com.arthuralexandryan.footballquiz.models.PlacesUFADTO
import com.arthuralexandryan.footballquiz.models.UserStatsDTO
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class RealmGameStateLocalDataSource : GameStateLocalDataSource {

    override fun getProgressSnapshot(): LocalProgressSnapshot {
        Realm.getDefaultInstance().use { realm ->
            val scores = ensureScores(realm)
            val franceAnswered = scores.top5Scores?.france_answered ?: 0
            val germanyAnswered = scores.top5Scores?.germany_answered ?: 0
            val italyAnswered = scores.top5Scores?.italy_answered ?: 0
            val englandAnswered = scores.top5Scores?.england_answered ?: 0
            val spainAnswered = scores.top5Scores?.spain_answered ?: 0
            val superCupAnswered = scores.ufaScores?.superCup_answered ?: 0
            val europaLeagueAnswered = scores.ufaScores?.europaLeague_answered ?: 0
            val championsLeagueAnswered = scores.ufaScores?.championsLeague_answered ?: 0
            val europeanChampionshipAnswered = scores.ufaScores?.europaChampionship_answered ?: 0
            val vsrmAnswered = scores.vsRM_answered
            val vsrbAnswered = scores.vsRB_answered
            val top5Answered = scores.top5_answered
            val uefaAnswered = scores.ufa_answered
            val worldAnswered = scores.world_answered
            val versusAnswered = vsrmAnswered + vsrbAnswered
            val totalScore = top5Answered + uefaAnswered + worldAnswered + versusAnswered

            return LocalProgressSnapshot(
                franceAnswered = franceAnswered,
                germanyAnswered = germanyAnswered,
                italyAnswered = italyAnswered,
                englandAnswered = englandAnswered,
                spainAnswered = spainAnswered,
                superCupAnswered = superCupAnswered,
                europaLeagueAnswered = europaLeagueAnswered,
                championsLeagueAnswered = championsLeagueAnswered,
                europeanChampionshipAnswered = europeanChampionshipAnswered,
                vsrmAnswered = vsrmAnswered,
                vsrbAnswered = vsrbAnswered,
                top5Answered = top5Answered,
                uefaAnswered = uefaAnswered,
                worldAnswered = worldAnswered,
                versusAnswered = versusAnswered,
                totalScore = totalScore
            )
        }
    }

    override fun getUserStats(user: AuthenticatedUser): UserStatsDTO {
        Realm.getDefaultInstance().use { realm ->
            val scores = ensureScores(realm)
            val totalScore = scores.top5_answered + scores.ufa_answered + scores.world_answered +
                scores.vsRM_answered + scores.vsRB_answered

            return UserStatsDTO(
                email = user.email.orEmpty(),
                gameState = GameState(
                    id = scores.id,
                    total = totalScore,
                    top5 = scores.top5,
                    top5_answered = scores.top5_answered,
                    ufa = scores.ufa,
                    ufa_answered = scores.ufa_answered,
                    world = scores.world,
                    world_answered = scores.world_answered,
                    vsRM = scores.vsRM,
                    vsRM_answered = scores.vsRM_answered,
                    vsRB = scores.vsRB,
                    vsRB_answered = scores.vsRB_answered,
                    top5Scores = scores.top5Scores?.let {
                        PlacesTop5DTO(
                            france_score = it.france_score,
                            france_answered = it.france_answered,
                            germany_score = it.germany_score,
                            germany_answered = it.germany_answered,
                            italy_score = it.italy_score,
                            italy_answered = it.italy_answered,
                            england_score = it.england_score,
                            england_answered = it.england_answered,
                            spain_score = it.spain_score,
                            spain_answered = it.spain_answered
                        )
                    },
                    ufaScores = scores.ufaScores?.let {
                        PlacesUFADTO(
                            superCup_score = it.superCup_score,
                            superCup_answered = it.superCup_answered,
                            europaLeague_score = it.europaLeague_score,
                            europaLeague_answered = it.europaLeague_answered,
                            europaChampionship_score = it.europaChampionship_score,
                            europaChampionship_answered = it.europaChampionship_answered,
                            championsLeague_score = it.championsLeague_score,
                            championsLeague_answered = it.championsLeague_answered
                        )
                    }
                ),
                name = user.displayName.orEmpty(),
                regId = user.id
            )
        }
    }

    override fun resetScoresToDefault() {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction { transactionRealm ->
                transactionRealm.insertOrUpdate(defaultScores())
            }
        }
    }

    override fun restoreStats(gameState: GameState) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction { transactionRealm ->
                val scores = transactionRealm.where(FQ_Scores::class.java).findFirst()
                    ?: transactionRealm.createObject(FQ_Scores::class.java, gameState.id)

                scores.fq_all = gameState.total
                scores.top5 = gameState.top5
                scores.top5_answered = gameState.top5_answered
                scores.ufa = gameState.ufa
                scores.ufa_answered = gameState.ufa_answered
                scores.world = gameState.world
                scores.world_answered = gameState.world_answered
                scores.vsRM = gameState.vsRM
                scores.vsRM_answered = gameState.vsRM_answered
                scores.vsRB = gameState.vsRB
                scores.vsRB_answered = gameState.vsRB_answered

                val top5 = scores.top5Scores ?: transactionRealm.createObject(PlacesTop5Obj::class.java, 0)
                gameState.top5Scores?.let { dto ->
                    top5.france_score = dto.france_score
                    top5.france_answered = dto.france_answered
                    top5.germany_score = dto.germany_score
                    top5.germany_answered = dto.germany_answered
                    top5.italy_score = dto.italy_score
                    top5.italy_answered = dto.italy_answered
                    top5.england_score = dto.england_score
                    top5.england_answered = dto.england_answered
                    top5.spain_score = dto.spain_score
                    top5.spain_answered = dto.spain_answered
                } ?: run {
                    top5.france_score = 40
                    top5.france_answered = 0
                    top5.germany_score = 40
                    top5.germany_answered = 0
                    top5.italy_score = 40
                    top5.italy_answered = 0
                    top5.england_score = 40
                    top5.england_answered = 0
                    top5.spain_score = 40
                    top5.spain_answered = 0
                }
                scores.top5Scores = top5

                val ufa = scores.ufaScores ?: transactionRealm.createObject(PlacesUFAObj::class.java, 0)
                gameState.ufaScores?.let { dto ->
                    ufa.superCup_score = dto.superCup_score
                    ufa.superCup_answered = dto.superCup_answered
                    ufa.europaLeague_score = dto.europaLeague_score
                    ufa.europaLeague_answered = dto.europaLeague_answered
                    ufa.europaChampionship_score = dto.europaChampionship_score
                    ufa.europaChampionship_answered = dto.europaChampionship_answered
                    ufa.championsLeague_score = dto.championsLeague_score
                    ufa.championsLeague_answered = dto.championsLeague_answered
                } ?: run {
                    ufa.championsLeague_score = 20
                    ufa.championsLeague_answered = 0
                    ufa.europaChampionship_score = 10
                    ufa.europaChampionship_answered = 0
                    ufa.europaLeague_score = 20
                    ufa.europaLeague_answered = 0
                    ufa.superCup_score = 20
                    ufa.superCup_answered = 0
                }
                scores.ufaScores = ufa

                transactionRealm.insertOrUpdate(scores)
            }
        }
    }

    override suspend fun resetAllProgress(): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransactionAsync({ transactionRealm ->
                transactionRealm.where(FQ_Scores::class.java).findAll().deleteAllFromRealm()
                disableAllAnswers(transactionRealm, DB_France::class.java)
                disableAllAnswers(transactionRealm, DB_Germany::class.java)
                disableAllAnswers(transactionRealm, DB_Italy::class.java)
                disableAllAnswers(transactionRealm, DB_England::class.java)
                disableAllAnswers(transactionRealm, DB_Spain::class.java)
                disableAllAnswers(transactionRealm, DB_Super_Cup::class.java)
                disableAllAnswers(transactionRealm, DB_Europa_League::class.java)
                disableAllAnswers(transactionRealm, DB_Europ_Championship::class.java)
                disableAllAnswers(transactionRealm, DB_Champions_League::class.java)
                disableAllAnswers(transactionRealm, DB_World_Championship::class.java)
                disableAllAnswers(transactionRealm, DB_VS_Ronaldo_Messi::class.java)
                disableAllAnswers(transactionRealm, DB_VS_RealM_Barcelona::class.java)
                transactionRealm.insertOrUpdate(defaultScores())
            }, {
                if (!realm.isClosed) realm.close()
                continuation.resume(Result.success(Unit))
            }, { error ->
                Log.e(LOG_DB, "resetAllProgress: failed to reset local Realm data", error)
                if (!realm.isClosed) realm.close()
                continuation.resume(Result.failure(error))
            })
        }
    }

    private fun ensureScores(realm: Realm): FQ_Scores {
        var scores = realm.where(FQ_Scores::class.java).findFirst()
        if (scores == null) {
            realm.executeTransaction { transactionRealm ->
                if (transactionRealm.where(FQ_Scores::class.java).findFirst() == null) {
                    transactionRealm.insertOrUpdate(defaultScores())
                }
            }
            scores = realm.where(FQ_Scores::class.java).findFirst()
        }
        return scores ?: defaultScores()
    }

    private fun defaultScores(): FQ_Scores {
        return FQ_Scores().apply {
            id = 0
            fq_all = 0
            top5 = 200
            top5_answered = 0
            ufa = 70
            ufa_answered = 0
            world = 30
            world_answered = 0
            vsRM = 50
            vsRM_answered = 0
            vsRB = 50
            vsRB_answered = 0
            top5Scores = PlacesTop5Obj().apply {
                id = 0
                france_score = 40
                france_answered = 0
                germany_score = 40
                germany_answered = 0
                italy_score = 40
                italy_answered = 0
                england_score = 40
                england_answered = 0
                spain_score = 40
                spain_answered = 0
            }
            ufaScores = PlacesUFAObj().apply {
                id = 0
                championsLeague_score = 20
                championsLeague_answered = 0
                europaChampionship_score = 10
                europaChampionship_answered = 0
                europaLeague_score = 20
                europaLeague_answered = 0
                superCup_score = 20
                superCup_answered = 0
            }
        }
    }

    private fun <T> disableAllAnswers(realm: Realm, modelClass: Class<T>): RealmResults<T>
        where T : RealmModel, T : DB_Base {
        val db = realm.where(modelClass).findAll()
        db.forEach { it.isAnswered = false }
        return db
    }
}
