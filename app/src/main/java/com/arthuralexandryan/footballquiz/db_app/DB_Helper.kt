package com.arthuralexandryan.footballquiz.db_app

import android.app.Activity
import android.util.Log
import com.arthuralexandryan.footballquiz.constants.Constant.LOG_DB
import com.arthuralexandryan.footballquiz.db_app.Score.FQ_Scores
import com.arthuralexandryan.footballquiz.db_app.Score.Objects.PlacesTop5Obj
import com.arthuralexandryan.footballquiz.db_app.Score.Objects.PlacesUFAObj
import com.arthuralexandryan.footballquiz.db_app.Top5.*
import com.arthuralexandryan.footballquiz.db_app.UFA.*
import com.arthuralexandryan.footballquiz.db_app.Versus.DB_VS_RealM_Barcelona
import com.arthuralexandryan.footballquiz.db_app.Versus.DB_VS_Ronaldo_Messi
import com.arthuralexandryan.footballquiz.db_app.World.DB_World_Championship
import com.arthuralexandryan.footballquiz.interfaces.Check
import com.arthuralexandryan.footballquiz.interfaces.ResetGame
import com.arthuralexandryan.footballquiz.models.Harc
import com.arthuralexandryan.footballquiz.models.ScoreboardModel
import com.arthuralexandryan.footballquiz.utils.CategoryType
import com.arthuralexandryan.footballquiz.utils.getOpenedPlace
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmResults

class DB_Helper @JvmOverloads constructor(
    private val activity: Activity? = null,
    private val categoryType: CategoryType? = null,
    private val type: String? = null
) {
    private var resetGame: ResetGame? = null
    private var fq_cores: FQ_Scores? = null

    fun setAnswered(question: String) {
        val db = Realm.getDefaultInstance()
        when (type) {
            "France" -> db.executeTransaction { realm ->
                val item = realm.where(DB_France::class.java).equalTo("question", question).findFirst()!!
                item.isAnswered = true; realm.insertOrUpdate(item)
            }
            "Germany" -> db.executeTransaction { realm ->
                val item = realm.where(DB_Germany::class.java).equalTo("question", question).findFirst()!!
                item.isAnswered = true; realm.insertOrUpdate(item)
            }
            "Italy" -> db.executeTransaction { realm ->
                val item = realm.where(DB_Italy::class.java).equalTo("question", question).findFirst()!!
                item.isAnswered = true; realm.insertOrUpdate(item)
            }
            "England" -> db.executeTransaction { realm ->
                val item = realm.where(DB_England::class.java).equalTo("question", question).findFirst()!!
                item.isAnswered = true; realm.insertOrUpdate(item)
            }
            "Spain" -> db.executeTransaction { realm ->
                val item = realm.where(DB_Spain::class.java).equalTo("question", question).findFirst()!!
                item.isAnswered = true; realm.insertOrUpdate(item)
            }
            "Super Cup" -> db.executeTransaction { realm ->
                val item = realm.where(DB_Super_Cup::class.java).equalTo("question", question).findFirst()!!
                item.isAnswered = true; realm.insertOrUpdate(item)
            }
            "European League" -> db.executeTransaction { realm ->
                val item = realm.where(DB_Europa_League::class.java).equalTo("question", question).findFirst()!!
                item.isAnswered = true; realm.insertOrUpdate(item)
            }
            "Champions League" -> db.executeTransaction { realm ->
                val item = realm.where(DB_Champions_League::class.java).equalTo("question", question).findFirst()!!
                item.isAnswered = true; realm.insertOrUpdate(item)
            }
            "Europe Championship" -> db.executeTransaction { realm ->
                val item = realm.where(DB_Europ_Championship::class.java).equalTo("question", question).findFirst()!!
                item.isAnswered = true; realm.insertOrUpdate(item)
            }
            "World Cup" -> db.executeTransaction { realm ->
                val item = realm.where(DB_World_Championship::class.java).equalTo("question", question).findFirst()!!
                item.isAnswered = true; realm.insertOrUpdate(item)
            }
            "Versus R_M" -> db.executeTransaction { realm ->
                val item = realm.where(DB_VS_Ronaldo_Messi::class.java).equalTo("question", question).findFirst()!!
                item.isAnswered = true; realm.insertOrUpdate(item)
            }
            "Versus R_B" -> db.executeTransaction { realm ->
                val item = realm.where(DB_VS_RealM_Barcelona::class.java).equalTo("question", question).findFirst()!!
                item.isAnswered = true; realm.insertOrUpdate(item)
            }
        }
    }

    fun setPlaceScores(placeScoreAnswer: Int, placeScore: Int) {
        val db = Realm.getDefaultInstance()
        db.executeTransaction { realm ->
            when (type) {
                "France" -> { fq_cores = realm.where(FQ_Scores::class.java).findFirst(); fq_cores!!.top5Scores.france_answered = placeScoreAnswer; fq_cores!!.top5Scores.france_score = placeScore }
                "Germany" -> { fq_cores = realm.where(FQ_Scores::class.java).findFirst(); fq_cores!!.top5Scores.germany_answered = placeScoreAnswer; fq_cores!!.top5Scores.germany_score = placeScore }
                "Italy" -> { fq_cores = realm.where(FQ_Scores::class.java).findFirst(); fq_cores!!.top5Scores.italy_answered = placeScoreAnswer; fq_cores!!.top5Scores.italy_score = placeScore }
                "England" -> { fq_cores = realm.where(FQ_Scores::class.java).findFirst(); fq_cores!!.top5Scores.england_answered = placeScoreAnswer; fq_cores!!.top5Scores.england_score = placeScore }
                "Spain" -> { fq_cores = realm.where(FQ_Scores::class.java).findFirst(); fq_cores!!.top5Scores.spain_answered = placeScoreAnswer; fq_cores!!.top5Scores.spain_score = placeScore }
                "Super Cup" -> { fq_cores = realm.where(FQ_Scores::class.java).findFirst(); fq_cores!!.ufaScores.superCup_answered = placeScoreAnswer; fq_cores!!.ufaScores.superCup_score = placeScore }
                "European League" -> { fq_cores = realm.where(FQ_Scores::class.java).findFirst(); fq_cores!!.ufaScores.europaLeague_answered = placeScoreAnswer; fq_cores!!.ufaScores.europaLeague_score = placeScore }
                "Champions League" -> { fq_cores = realm.where(FQ_Scores::class.java).findFirst(); fq_cores!!.ufaScores.championsLeague_answered = placeScoreAnswer; fq_cores!!.ufaScores.championsLeague_score = placeScore }
                "Europe Championship" -> { fq_cores = realm.where(FQ_Scores::class.java).findFirst(); fq_cores!!.ufaScores.europaChampionship_answered = placeScoreAnswer; fq_cores!!.ufaScores.europaChampionship_score = placeScore }
                "World Cup" -> { fq_cores = realm.where(FQ_Scores::class.java).findFirst(); fq_cores!!.world_answered = placeScoreAnswer; fq_cores!!.world = placeScore }
                "Versus R_M" -> { fq_cores = realm.where(FQ_Scores::class.java).findFirst(); fq_cores!!.vsRM_answered = placeScoreAnswer; fq_cores!!.vsRM = placeScore }
                "Versus R_B" -> { fq_cores = realm.where(FQ_Scores::class.java).findFirst(); fq_cores!!.vsRB_answered = placeScoreAnswer; fq_cores!!.vsRB = placeScore }
            }
            realm.copyFromRealm(fq_cores!!)
        }
        if (!db.isClosed) db.close()
    }

    fun setCategoryScores() {
        val db = Realm.getDefaultInstance()
        db.executeTransaction { realm ->
            when (categoryType) {
                CategoryType.TOP5 -> { fq_cores = db.where(FQ_Scores::class.java).findFirst(); fq_cores!!.top5_answered = fq_cores!!.top5_answered + 1 }
                CategoryType.UEFA -> { fq_cores = db.where(FQ_Scores::class.java).findFirst(); fq_cores!!.ufa_answered = fq_cores!!.ufa_answered + 1 }
                CategoryType.WORLD_CUP -> { fq_cores = db.where(FQ_Scores::class.java).findFirst(); fq_cores!!.world_answered = fq_cores!!.world_answered + 1 }
                CategoryType.RON_MES -> { fq_cores = db.where(FQ_Scores::class.java).findFirst(); fq_cores!!.vsRM_answered = fq_cores!!.vsRM_answered + 1 }
                CategoryType.REAL_BARCE -> { fq_cores = db.where(FQ_Scores::class.java).findFirst(); fq_cores!!.vsRB_answered = fq_cores!!.vsRB_answered + 1 }
                null -> {}
            }
            realm.copyFromRealm(fq_cores!!)
        }
        getTotalScore()
        getOpenedPlace(fq_cores!!.fq_all, activity ?: return)
    }

    fun deletePlaceScores(placeScoreAnswer: Int, placeScore: Int) {
        val db = Realm.getDefaultInstance()
        when (type) {
            "France" -> db.executeTransaction { realm -> realm.delete(DB_France::class.java) }
            "Germany" -> db.executeTransaction { realm -> val s = realm.where(FQ_Scores::class.java).findFirst()!!; s.top5Scores.germany_answered = placeScoreAnswer; s.top5Scores.germany_score = placeScore; realm.copyFromRealm(s) }
            "Italy" -> db.executeTransaction { realm -> val s = realm.where(FQ_Scores::class.java).findFirst()!!; s.top5Scores.italy_answered = placeScoreAnswer; s.top5Scores.italy_score = placeScore; realm.copyFromRealm(s) }
            "England" -> db.executeTransaction { realm -> val s = realm.where(FQ_Scores::class.java).findFirst()!!; s.top5Scores.england_answered = placeScoreAnswer; s.top5Scores.england_score = placeScore; realm.copyFromRealm(s) }
            "Spain" -> db.executeTransaction { realm -> val s = realm.where(FQ_Scores::class.java).findFirst()!!; s.top5Scores.spain_answered = placeScoreAnswer; s.top5Scores.spain_score = placeScore; realm.copyFromRealm(s) }
            "Super Cup" -> db.executeTransaction { realm -> val s = realm.where(FQ_Scores::class.java).findFirst()!!; s.ufaScores.superCup_answered = placeScoreAnswer; s.ufaScores.superCup_score = placeScore; realm.copyFromRealm(s) }
            "European League" -> db.executeTransaction { realm -> val s = realm.where(FQ_Scores::class.java).findFirst()!!; s.ufaScores.europaLeague_score = placeScoreAnswer; s.ufaScores.europaLeague_score = placeScore; realm.copyFromRealm(s) }
            "Champions League" -> db.executeTransaction { realm -> val s = realm.where(FQ_Scores::class.java).findFirst()!!; s.ufaScores.championsLeague_answered = placeScoreAnswer; s.ufaScores.championsLeague_score = placeScore; realm.copyFromRealm(s) }
            "Europe Championship" -> db.executeTransaction { realm -> val s = realm.where(FQ_Scores::class.java).findFirst()!!; s.ufaScores.europaChampionship_answered = placeScoreAnswer; s.ufaScores.europaChampionship_score = placeScore; realm.copyFromRealm(s) }
            "World Cup" -> db.executeTransaction { realm -> val s = realm.where(FQ_Scores::class.java).findFirst()!!; s.world_answered = placeScoreAnswer; s.world = placeScore; realm.copyFromRealm(s) }
            "Versus R_M" -> db.executeTransaction { realm -> val s = realm.where(FQ_Scores::class.java).findFirst()!!; s.vsRM_answered = placeScoreAnswer; s.vsRM = placeScore; realm.copyFromRealm(s) }
        }
    }

    private fun <T> setQuestions(harcer: List<Harc>, createItem: (Int, Harc) -> T, insertAll: (Realm, List<T>) -> Unit) {
        val items = harcer.mapIndexed { i, harc -> createItem(i, harc) }
        val db = Realm.getDefaultInstance()
        db.executeTransaction { realm -> insertAll(realm, items) }
        if (!db.isClosed) db.close()
    }

    fun setFranceQuestions(harcer: List<Harc>) = setQuestions(harcer, { i, h ->
        DB_France().apply { id = i; answer_A = h.patasxanner.a; answer_B = h.patasxanner.b; answer_C = h.patasxanner.c; answer_D = h.patasxanner.d; question = h.harc; right_answer = h.chist }
    }, { realm, items -> realm.copyToRealmOrUpdate(items) })

    fun setGermanyQuestions(harcer: List<Harc>) = setQuestions(harcer, { i, h ->
        DB_Germany().apply { id = i; answer_A = h.patasxanner.a; answer_B = h.patasxanner.b; answer_C = h.patasxanner.c; answer_D = h.patasxanner.d; question = h.harc; right_answer = h.chist }
    }, { realm, items -> realm.copyToRealmOrUpdate(items) })

    fun setItalyQuestions(harcer: List<Harc>) = setQuestions(harcer, { i, h ->
        DB_Italy().apply { id = i; answer_A = h.patasxanner.a; answer_B = h.patasxanner.b; answer_C = h.patasxanner.c; answer_D = h.patasxanner.d; question = h.harc; right_answer = h.chist }
    }, { realm, items -> realm.copyToRealmOrUpdate(items) })

    fun setEnglishQuestions(harcer: List<Harc>) = setQuestions(harcer, { i, h ->
        DB_England().apply { id = i; answer_A = h.patasxanner.a; answer_B = h.patasxanner.b; answer_C = h.patasxanner.c; answer_D = h.patasxanner.d; question = h.harc; right_answer = h.chist }
    }, { realm, items -> realm.copyToRealmOrUpdate(items) })

    fun setSpainQuestions(harcer: List<Harc>) = setQuestions(harcer, { i, h ->
        DB_Spain().apply { id = i; answer_A = h.patasxanner.a; answer_B = h.patasxanner.b; answer_C = h.patasxanner.c; answer_D = h.patasxanner.d; question = h.harc; right_answer = h.chist }
    }, { realm, items -> realm.copyToRealmOrUpdate(items) })

    fun setSuperCupQuestions(harcer: List<Harc>) = setQuestions(harcer, { i, h ->
        DB_Super_Cup().apply { id = i; answer_A = h.patasxanner.a; answer_B = h.patasxanner.b; answer_C = h.patasxanner.c; answer_D = h.patasxanner.d; question = h.harc; right_answer = h.chist }
    }, { realm, items -> realm.copyToRealmOrUpdate(items) })

    fun setEuropeanLeagueQuestions(harcer: List<Harc>) = setQuestions(harcer, { i, h ->
        DB_Europa_League().apply { id = i; answer_A = h.patasxanner.a; answer_B = h.patasxanner.b; answer_C = h.patasxanner.c; answer_D = h.patasxanner.d; question = h.harc; right_answer = h.chist }
    }, { realm, items -> realm.copyToRealmOrUpdate(items) })

    fun setEuropeanChampeonQuestions(harcer: List<Harc>) = setQuestions(harcer, { i, h ->
        DB_Europ_Championship().apply { id = i; answer_A = h.patasxanner.a; answer_B = h.patasxanner.b; answer_C = h.patasxanner.c; answer_D = h.patasxanner.d; question = h.harc; right_answer = h.chist }
    }, { realm, items -> realm.copyToRealmOrUpdate(items) })

    fun setChampionsQuestions(harcer: List<Harc>) = setQuestions(harcer, { i, h ->
        DB_Champions_League().apply { id = i; answer_A = h.patasxanner.a; answer_B = h.patasxanner.b; answer_C = h.patasxanner.c; answer_D = h.patasxanner.d; question = h.harc; right_answer = h.chist }
    }, { realm, items -> realm.copyToRealmOrUpdate(items) })

    fun setWorldQuestions(harcer: List<Harc>) = setQuestions(harcer, { i, h ->
        DB_World_Championship().apply { id = i; answer_A = h.patasxanner.a; answer_B = h.patasxanner.b; answer_C = h.patasxanner.c; answer_D = h.patasxanner.d; question = h.harc; right_answer = h.chist }
    }, { realm, items -> realm.copyToRealmOrUpdate(items) })

    fun setRMVersusQuestions(harcer: List<Harc>) = setQuestions(harcer, { i, h ->
        DB_VS_Ronaldo_Messi().apply { id = i; answer_A = h.patasxanner.a; answer_B = h.patasxanner.b; answer_C = h.patasxanner.c; answer_D = h.patasxanner.d; question = h.harc; right_answer = h.chist }
    }, { realm, items -> realm.copyToRealmOrUpdate(items) })

    fun setRBVersusQuestions(harcer: List<Harc>) = setQuestions(harcer, { i, h ->
        DB_VS_RealM_Barcelona().apply { id = i; answer_A = h.patasxanner.a; answer_B = h.patasxanner.b; answer_C = h.patasxanner.c; answer_D = h.patasxanner.d; question = h.harc; right_answer = h.chist }
    }, { realm, items -> realm.copyToRealmOrUpdate(items) })

    fun setDefaultAllScores() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync(Realm.Transaction { realm1 ->
            val scores = FQ_Scores().apply {
                id = 0; fq_all = 0; top5 = 200; top5_answered = 0
                ufa = 70; ufa_answered = 0; world = 30; world_answered = 0
                vsRM = 50; vsRM_answered = 0; vsRB = 50; vsRB_answered = 0
                top5Scores = PlacesTop5Obj().apply {
                    france_score = 40; france_answered = 0; germany_score = 40; germany_answered = 0
                    italy_score = 40; italy_answered = 0; england_score = 40; england_answered = 0
                    spain_score = 40; spain_answered = 0
                }
                ufaScores = PlacesUFAObj().apply {
                    championsLeague_score = 20; championsLeague_answered = 0
                    europaChampionship_score = 10; europaChampionship_answered = 0
                    europaLeague_score = 20; europaLeague_answered = 0
                    superCup_score = 20; superCup_answered = 0
                }
            }
            realm1.insertOrUpdate(scores)
        }, Realm.Transaction.OnSuccess {
            Log.d(LOG_DB, "getAllScores success")
            realm.close()
        })
    }

    val top5AnsweredScores: Int
        get() = Realm.getDefaultInstance().where(FQ_Scores::class.java).findFirst()!!.top5_answered

    fun getUFAAnsweredScores(): Int =
        Realm.getDefaultInstance().where(FQ_Scores::class.java).findFirst()!!.ufa_answered

    fun getWorldAnsweredScores(): Int =
        Realm.getDefaultInstance().where(FQ_Scores::class.java).findFirst()!!.world_answered

    fun getVersusAnsweredScores(): Int =
        Realm.getDefaultInstance().where(FQ_Scores::class.java).findFirst()!!.vsRM_answered

    fun getTotalScore(): Int {
        val scores = Realm.getDefaultInstance().where(FQ_Scores::class.java).findFirst()!!
        val allScore = scores.top5_answered + scores.ufa_answered + scores.world_answered + scores.vsRM_answered
        Log.e("FQ_Log", "all score: 0")
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm1 -> scores.fq_all = allScore; realm1.insertOrUpdate(scores) }
        realm.close()
        return allScore
    }

    fun getTotalScoreText(): String {
        Log.e("FQ_Log", getTotalScore().toString())
        return getTotalScore().toString()
    }

    private fun <T : RealmModel> disableAllAnswers(realm: Realm, tClass: Class<T>): RealmResults<T> {
        val db = realm.where(tClass).findAll()
        db.forEach { (it as DB_Base).isAnswered = false }
        return db
    }

    fun resetPlace(answeredScores: Int, reset: ResetGame, isForce: Boolean) {
        val realm = Realm.getDefaultInstance()
        this.resetGame = reset
        try {
            when (type) {
                "France" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_France::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()!!
                    s.top5Scores.france_score = 40; s.top5Scores.france_answered = 0
                    s.top5_answered -= answeredScores; r.insertOrUpdate(s)
                    resetGame!!.reset(ScoreboardModel(40, 0, s.top5_answered, s.top5), isForce)
                }
                "Germany" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Germany::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()!!
                    s.top5Scores.germany_score = 40; s.top5Scores.germany_answered = 0
                    s.top5_answered -= answeredScores; r.insertOrUpdate(s)
                    resetGame!!.reset(ScoreboardModel(40, 0, s.top5_answered, s.top5), isForce)
                }
                "Italy" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Italy::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()!!
                    s.top5Scores.italy_score = 40; s.top5Scores.italy_answered = 0
                    s.top5_answered -= answeredScores; r.insertOrUpdate(s)
                    resetGame!!.reset(ScoreboardModel(40, 0, s.top5_answered, s.top5), isForce)
                }
                "England" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_England::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()!!
                    s.top5Scores.england_score = 40; s.top5Scores.england_answered = 0
                    s.top5_answered -= answeredScores; r.insertOrUpdate(s)
                    resetGame!!.reset(ScoreboardModel(40, 0, s.top5_answered, s.top5), isForce)
                }
                "Spain" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Spain::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()!!
                    s.top5Scores.spain_score = 40; s.top5Scores.spain_answered = 0
                    s.top5_answered -= answeredScores; r.insertOrUpdate(s)
                    resetGame!!.reset(ScoreboardModel(40, 0, s.top5_answered, s.top5), isForce)
                }
                "Super Cup" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Super_Cup::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()!!
                    s.ufaScores.superCup_score = 20; s.ufaScores.superCup_answered = 0
                    s.ufa_answered -= answeredScores; r.insertOrUpdate(s)
                    resetGame!!.reset(ScoreboardModel(20, 0, s.ufa_answered, s.ufa), isForce)
                }
                "European League" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Europa_League::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()!!
                    s.ufaScores.europaLeague_score = 20; s.ufaScores.europaLeague_answered = 0
                    s.ufa_answered -= answeredScores; r.insertOrUpdate(s)
                    resetGame!!.reset(ScoreboardModel(20, 0, s.ufa_answered, s.ufa), isForce)
                }
                "Champions League" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Champions_League::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()!!
                    s.ufaScores.championsLeague_score = 20; s.ufaScores.championsLeague_answered = 0
                    s.ufa_answered -= answeredScores; r.insertOrUpdate(s)
                    resetGame!!.reset(ScoreboardModel(20, 0, s.ufa_answered, s.ufa), isForce)
                }
                "Europe Championship" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Europ_Championship::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()!!
                    s.ufaScores.europaChampionship_score = 10; s.ufaScores.europaChampionship_answered = 0
                    s.ufa_answered -= answeredScores; r.insertOrUpdate(s)
                    resetGame!!.reset(ScoreboardModel(10, 0, s.ufa_answered, s.ufa), isForce)
                }
                "World Cup" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_World_Championship::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()!!
                    s.world = 30; s.world_answered = 0; r.insertOrUpdate(s)
                    resetGame!!.reset(ScoreboardModel(30, 0, s.world_answered, s.world), isForce)
                }
                "Versus R_M" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_VS_Ronaldo_Messi::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()!!
                    s.vsRM = 50; s.vsRM_answered = 0; r.insertOrUpdate(s)
                    resetGame!!.reset(ScoreboardModel(50, 0, s.vsRM_answered, s.vsRM), isForce)
                }
                "Versus R_B" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_VS_RealM_Barcelona::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()!!
                    s.vsRB = 50; s.vsRB_answered = 0; r.insertOrUpdate(s)
                    resetGame!!.reset(ScoreboardModel(50, 0, s.vsRB_answered, s.vsRB), isForce)
                }
            }
        } finally {
            realm.close()
        }
    }

    val top5Scores: List<ScoreboardModel>
        get() = listOf(getFranceScoreBoard(), getGermanyScoreBoard(), getItalyScoreBoard(), getEnglandScoreBoard(), getSpainScoreBoard())

    fun getUFAScores(): List<ScoreboardModel> =
        listOf(getSuperScoreBoard(), getEuropaLeagueScoreBoard(), getChampionsScoreBoard(), getEuroScoreBoard())

    val worldScores: List<ScoreboardModel>
        get() = listOf(getWorldScoreBoard())

    private fun fq(): FQ_Scores = Realm.getDefaultInstance().where(FQ_Scores::class.java).findFirst()!!

    private fun getFranceScoreBoard() = fq().let { ScoreboardModel(it.top5Scores.france_score, it.top5Scores.france_answered, it.top5_answered, it.top5) }
    private fun getGermanyScoreBoard() = fq().let { ScoreboardModel(it.top5Scores.germany_score, it.top5Scores.germany_answered, it.top5_answered, it.top5) }
    private fun getItalyScoreBoard() = fq().let { ScoreboardModel(it.top5Scores.italy_score, it.top5Scores.italy_answered, it.top5_answered, it.top5) }
    private fun getEnglandScoreBoard() = fq().let { ScoreboardModel(it.top5Scores.england_score, it.top5Scores.england_answered, it.top5_answered, it.top5) }
    private fun getSpainScoreBoard() = fq().let { ScoreboardModel(it.top5Scores.spain_score, it.top5Scores.spain_answered, it.top5_answered, it.top5) }
    private fun getSuperScoreBoard() = fq().let { ScoreboardModel(it.ufaScores.superCup_score, it.ufaScores.superCup_answered, it.ufa_answered, it.ufa) }
    private fun getEuropaLeagueScoreBoard() = fq().let { ScoreboardModel(it.ufaScores.europaLeague_score, it.ufaScores.europaLeague_answered, it.ufa_answered, it.ufa) }
    private fun getChampionsScoreBoard() = fq().let { ScoreboardModel(it.ufaScores.championsLeague_score, it.ufaScores.championsLeague_answered, it.ufa_answered, it.ufa) }
    private fun getEuroScoreBoard() = fq().let { ScoreboardModel(it.ufaScores.europaChampionship_score, it.ufaScores.europaChampionship_answered, it.ufa_answered, it.ufa) }
    private fun getWorldScoreBoard() = fq().let { ScoreboardModel(it.world, it.world_answered, it.world_answered, 30) }

    val ronMessiScoreBoard: ScoreboardModel
        get() = fq().let { ScoreboardModel(it.vsRM, it.vsRM_answered, it.vsRM_answered, 50) }

    val realBarcScoreBoard: ScoreboardModel
        get() = fq().let { ScoreboardModel(it.vsRB, it.vsRB_answered, it.vsRB_answered, 50) }

    fun deleteAll(check: Check) {
        val mRealm = Realm.getDefaultInstance()
        mRealm.executeTransactionAsync(Realm.Transaction { realm ->
            realm.where(FQ_Scores::class.java).findAll().deleteAllFromRealm()
        }, Realm.Transaction.OnSuccess {
            mRealm.close()
            check.onCheck()
        })
    }
}
