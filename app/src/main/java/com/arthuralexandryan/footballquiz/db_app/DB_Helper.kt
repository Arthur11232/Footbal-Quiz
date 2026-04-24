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
import com.arthuralexandryan.footballquiz.models.IsSetQuestions
import com.arthuralexandryan.footballquiz.models.QuestionModel
import com.arthuralexandryan.footballquiz.models.ScoreboardModel
import com.arthuralexandryan.footballquiz.models.places
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
        try {
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
        } finally {
            if (!db.isClosed) db.close()
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
        try {
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
            activity?.let { getOpenedPlace(fq_cores!!.fq_all, it) }
        } finally {
            if (!db.isClosed) db.close()
        }
    }

    fun deletePlaceScores(placeScoreAnswer: Int, placeScore: Int) {
        val db = Realm.getDefaultInstance()
        db.executeTransaction { realm ->
            val s = realm.where(FQ_Scores::class.java).findFirst()
            if (s != null) {
                when (type) {
                    "France" -> realm.delete(DB_France::class.java)
                    "Germany" -> { s.top5Scores.germany_answered = placeScoreAnswer; s.top5Scores.germany_score = placeScore }
                    "Italy" -> { s.top5Scores.italy_answered = placeScoreAnswer; s.top5Scores.italy_score = placeScore }
                    "England" -> { s.top5Scores.england_answered = placeScoreAnswer; s.top5Scores.england_score = placeScore }
                    "Spain" -> { s.top5Scores.spain_answered = placeScoreAnswer; s.top5Scores.spain_score = placeScore }
                    "Super Cup" -> { s.ufaScores.superCup_answered = placeScoreAnswer; s.ufaScores.superCup_score = placeScore }
                    "European League" -> { s.ufaScores.europaLeague_score = placeScoreAnswer; s.ufaScores.europaLeague_score = placeScore }
                    "Champions League" -> { s.ufaScores.championsLeague_answered = placeScoreAnswer; s.ufaScores.championsLeague_score = placeScore }
                    "Europe Championship" -> { s.ufaScores.europaChampionship_answered = placeScoreAnswer; s.ufaScores.europaChampionship_score = placeScore }
                    "World Cup" -> { s.world_answered = placeScoreAnswer; s.world = placeScore }
                    "Versus R_M" -> { s.vsRM_answered = placeScoreAnswer; s.vsRM = placeScore }
                }
                realm.insertOrUpdate(s)
            }
        }
        db.close()
    }
    fun setQuestionsToDB(modelList: List<QuestionModel>, isNew: Boolean, isSet: IsSetQuestions) {
        val db = Realm.getDefaultInstance()

        db.executeTransaction { realm ->
            setFranceQuestions(realm, checkPlaceQuestions(places.FRANCE.place, modelList))
            setGermanyQuestions(realm, checkPlaceQuestions(places.GERMANY.place, modelList))
            setItalyQuestions(realm, checkPlaceQuestions(places.ITALY.place, modelList))
            setEnglishQuestions(realm, checkPlaceQuestions(places.ENGLISH.place, modelList))
            setSpainQuestions(realm, checkPlaceQuestions(places.SPAIN.place, modelList))
            setSuperCupQuestions(realm, checkPlaceQuestions(places.SUPER_CUP.place, modelList))
            setEuropeanLeagueQuestions(realm, checkPlaceQuestions(places.EUROPA_LEAGUE.place, modelList))
            setEuropeanChampeonQuestions(realm, checkPlaceQuestions(places.EUROPA_CHAMPIONS.place, modelList))
            setChampionsQuestions(realm, checkPlaceQuestions(places.CHAMPIONS.place, modelList))
            setWorldQuestions(realm, checkPlaceQuestions(places.WORLD.place, modelList))
            setRMVersusQuestions(realm, checkPlaceQuestions(places.RON_MESSI.place, modelList))
            setRBVersusQuestions(realm, checkPlaceQuestions(places.REAL_BARCA.place, modelList))

            if (isNew) {
                setDefaultAllScores(realm)
            }
        }

        if (!db.isClosed) {
            db.close()
        }

        isSet.finish()
    }

    private fun checkPlaceQuestions(place: String, modelList: List<QuestionModel>): List<QuestionModel> {
        // В Kotlin это делается одной строчкой через filter
        return modelList.filter { it.place == place }
    }

    private fun <T : RealmModel> setQuestions(realm: Realm, modelList: List<QuestionModel>, createItem: (Int, QuestionModel) -> T) {
        val items = modelList.mapIndexed { i, m -> createItem(i, m) }
        realm.copyToRealmOrUpdate(items)
    }

    fun setFranceQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_France().apply { id = i; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: "" }
    }
    fun setGermanyQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Germany().apply { id = i; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: "" }
    }
    fun setItalyQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Italy().apply { id = i; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: "" }
    }
    fun setEnglishQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_England().apply { id = i; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: "" }
    }
    fun setSpainQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Spain().apply { id = i; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: "" }
    }
    fun setSuperCupQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Super_Cup().apply { id = i; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: "" }
    }
    fun setEuropeanLeagueQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Europa_League().apply { id = i; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: "" }
    }
    fun setEuropeanChampeonQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Europ_Championship().apply { id = i; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: "" }
    }
    fun setChampionsQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Champions_League().apply { id = i; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: "" }
    }
    fun setWorldQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_World_Championship().apply { id = i; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: "" }
    }
    fun setRMVersusQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_VS_Ronaldo_Messi().apply { id = i; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: "" }
    }
    fun setRBVersusQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_VS_RealM_Barcelona().apply { id = i; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: "" }
    }

    fun setDefaultAllScores(realm: Realm) {
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
            realm.insertOrUpdate(scores)
    }

    val top5AnsweredScores: Int
        get() = Realm.getDefaultInstance().use { realm ->
            realm.where(FQ_Scores::class.java).findFirst()?.top5_answered ?: 0
        }

    fun getUFAAnsweredScores(): Int =
        Realm.getDefaultInstance().use { realm ->
            realm.where(FQ_Scores::class.java).findFirst()?.ufa_answered ?: 0
        }

    fun getWorldAnsweredScores(): Int =
        Realm.getDefaultInstance().use { realm ->
            realm.where(FQ_Scores::class.java).findFirst()?.world_answered ?: 0
        }

    fun getVersusAnsweredScores(): Int =
        Realm.getDefaultInstance().use { realm ->
            realm.where(FQ_Scores::class.java).findFirst()?.vsRM_answered ?: 0
        }

    fun getTotalScore(): Int {
        val realm = Realm.getDefaultInstance()
        var allScore = 0
        realm.executeTransaction { r ->
            var scores = r.where(FQ_Scores::class.java).findFirst()
            if (scores == null) {
                setDefaultAllScores(r)
                scores = r.where(FQ_Scores::class.java).findFirst()
            }
            
            if (scores != null) {
                allScore = scores.top5_answered + scores.ufa_answered + scores.world_answered + scores.vsRM_answered
                scores.fq_all = allScore
                r.insertOrUpdate(scores)
            }
        }
        Log.e("FQ_Log", "all score: $allScore")
        realm.close()
        return allScore
    }

    fun getTotalScoreText(): String {
        Log.e("FQ_Log", getTotalScore().toString())
        return getTotalScore().toString()
    }

    private fun <T> disableAllAnswers(realm: Realm, tClass: Class<T>): RealmResults<T>
            where T : RealmModel, T : DB_Base {
        val db = realm.where(tClass).findAll()
        db.forEach { it.isAnswered = false }
        return db
    }

    fun resetPlace(answeredScores: Int, reset: ResetGame, isForce: Boolean) {
        val realm = Realm.getDefaultInstance()
        this.resetGame = reset
        try {
            when (type) {
                "France" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_France::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()
                    if (s != null) {
                        s.top5Scores.france_score = 40; s.top5Scores.france_answered = 0
                        s.top5_answered -= answeredScores; r.insertOrUpdate(s)
                        resetGame!!.reset(ScoreboardModel(40, 0, s.top5_answered, s.top5), isForce)
                    }
                }
                "Germany" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Germany::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()
                    if (s != null) {
                        s.top5Scores.germany_score = 40; s.top5Scores.germany_answered = 0
                        s.top5_answered -= answeredScores; r.insertOrUpdate(s)
                        resetGame!!.reset(ScoreboardModel(40, 0, s.top5_answered, s.top5), isForce)
                    }
                }
                "Italy" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Italy::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()
                    if (s != null) {
                        s.top5Scores.italy_score = 40; s.top5Scores.italy_answered = 0
                        s.top5_answered -= answeredScores; r.insertOrUpdate(s)
                        resetGame!!.reset(ScoreboardModel(40, 0, s.top5_answered, s.top5), isForce)
                    }
                }
                "England" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_England::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()
                    if (s != null) {
                        s.top5Scores.england_score = 40; s.top5Scores.england_answered = 0
                        s.top5_answered -= answeredScores; r.insertOrUpdate(s)
                        resetGame!!.reset(ScoreboardModel(40, 0, s.top5_answered, s.top5), isForce)
                    }
                }
                "Spain" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Spain::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()
                    if (s != null) {
                        s.top5Scores.spain_score = 40; s.top5Scores.spain_answered = 0
                        s.top5_answered -= answeredScores; r.insertOrUpdate(s)
                        resetGame!!.reset(ScoreboardModel(40, 0, s.top5_answered, s.top5), isForce)
                    }
                }
                "Super Cup" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Super_Cup::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()
                    if (s != null) {
                        s.ufaScores.superCup_score = 20; s.ufaScores.superCup_answered = 0
                        s.ufa_answered -= answeredScores; r.insertOrUpdate(s)
                        resetGame!!.reset(ScoreboardModel(20, 0, s.ufa_answered, s.ufa), isForce)
                    }
                }
                "European League" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Europa_League::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()
                    if (s != null) {
                        s.ufaScores.europaLeague_score = 20; s.ufaScores.europaLeague_answered = 0
                        s.ufa_answered -= answeredScores; r.insertOrUpdate(s)
                        resetGame!!.reset(ScoreboardModel(20, 0, s.ufa_answered, s.ufa), isForce)
                    }
                }
                "Champions League" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Champions_League::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()
                    if (s != null) {
                        s.ufaScores.championsLeague_score = 20; s.ufaScores.championsLeague_answered = 0
                        s.ufa_answered -= answeredScores; r.insertOrUpdate(s)
                        resetGame!!.reset(ScoreboardModel(20, 0, s.ufa_answered, s.ufa), isForce)
                    }
                }
                "Europe Championship" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_Europ_Championship::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()
                    if (s != null) {
                        s.ufaScores.europaChampionship_score = 10; s.ufaScores.europaChampionship_answered = 0
                        s.ufa_answered -= answeredScores; r.insertOrUpdate(s)
                        resetGame!!.reset(ScoreboardModel(10, 0, s.ufa_answered, s.ufa), isForce)
                    }
                }
                "World Cup" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_World_Championship::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()
                    if (s != null) {
                        s.world = 30; s.world_answered = 0; r.insertOrUpdate(s)
                        resetGame!!.reset(ScoreboardModel(30, 0, s.world_answered, s.world), isForce)
                    }
                }
                "Versus R_M" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_VS_Ronaldo_Messi::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()
                    if (s != null) {
                        s.vsRM = 50; s.vsRM_answered = 0; r.insertOrUpdate(s)
                        resetGame!!.reset(ScoreboardModel(50, 0, s.vsRM_answered, s.vsRM), isForce)
                    }
                }
                "Versus R_B" -> realm.executeTransaction { r ->
                    r.insertOrUpdate(disableAllAnswers(realm, DB_VS_RealM_Barcelona::class.java))
                    val s = r.where(FQ_Scores::class.java).findFirst()
                    if (s != null) {
                        s.vsRB = 50; s.vsRB_answered = 0; r.insertOrUpdate(s)
                        resetGame!!.reset(ScoreboardModel(50, 0, s.vsRB_answered, s.vsRB), isForce)
                    }
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


    private fun getFranceScoreBoard() = Realm.getDefaultInstance().use { realm ->
        val it = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 200)
        ScoreboardModel(it.top5Scores.france_score, it.top5Scores.france_answered, it.top5_answered, it.top5)
    }

    private fun getGermanyScoreBoard() = Realm.getDefaultInstance().use { realm ->
        val it = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 200)
        ScoreboardModel(it.top5Scores.germany_score, it.top5Scores.germany_answered, it.top5_answered, it.top5)
    }

    private fun getItalyScoreBoard() = Realm.getDefaultInstance().use { realm ->
        val it = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 200)
        ScoreboardModel(it.top5Scores.italy_score, it.top5Scores.italy_answered, it.top5_answered, it.top5)
    }

    private fun getEnglandScoreBoard() = Realm.getDefaultInstance().use { realm ->
        val it = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 200)
        ScoreboardModel(it.top5Scores.england_score, it.top5Scores.england_answered, it.top5_answered, it.top5)
    }

    private fun getSpainScoreBoard() = Realm.getDefaultInstance().use { realm ->
        val it = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 200)
        ScoreboardModel(it.top5Scores.spain_score, it.top5Scores.spain_answered, it.top5_answered, it.top5)
    }

    private fun getSuperScoreBoard() = Realm.getDefaultInstance().use { realm ->
        val it = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 70)
        ScoreboardModel(it.ufaScores.superCup_score, it.ufaScores.superCup_answered, it.ufa_answered, it.ufa)
    }

    private fun getEuropaLeagueScoreBoard() = Realm.getDefaultInstance().use { realm ->
        val it = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 70)
        ScoreboardModel(it.ufaScores.europaLeague_score, it.ufaScores.europaLeague_answered, it.ufa_answered, it.ufa)
    }

    private fun getChampionsScoreBoard() = Realm.getDefaultInstance().use { realm ->
        val it = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 70)
        ScoreboardModel(it.ufaScores.championsLeague_score, it.ufaScores.championsLeague_answered, it.ufa_answered, it.ufa)
    }

    private fun getEuroScoreBoard() = Realm.getDefaultInstance().use { realm ->
        val it = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 70)
        ScoreboardModel(it.ufaScores.europaChampionship_score, it.ufaScores.europaChampionship_answered, it.ufa_answered, it.ufa)
    }

    private fun getWorldScoreBoard() = Realm.getDefaultInstance().use { realm ->
        val it = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 30)
        ScoreboardModel(it.world, it.world_answered, it.world_answered, 30)
    }

    val ronMessiScoreBoard: ScoreboardModel
        get() = Realm.getDefaultInstance().use { realm ->
            val it = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 50)
            ScoreboardModel(it.vsRM, it.vsRM_answered, it.vsRM_answered, 50)
        }

    val realBarcScoreBoard: ScoreboardModel
        get() = Realm.getDefaultInstance().use { realm ->
            val it = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 50)
            ScoreboardModel(it.vsRB, it.vsRB_answered, it.vsRB_answered, 50)
        }

    fun restoreStats(gameState: com.arthuralexandryan.footballquiz.models.GameState) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { r ->
            val scores = r.where(FQ_Scores::class.java).findFirst() ?: FQ_Scores().apply { id = 0 }
            
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
            
            // Restore Top 5 sub-scores
            gameState.top5Scores?.let { dto ->
                val top5 = scores.top5Scores ?: r.createObject(PlacesTop5Obj::class.java, 0)
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
                scores.top5Scores = top5
            }
            
            // Restore UEFA sub-scores
            gameState.ufaScores?.let { dto ->
                val ufa = scores.ufaScores ?: r.createObject(PlacesUFAObj::class.java, 0)
                ufa.superCup_score = dto.superCup_score
                ufa.superCup_answered = dto.superCup_answered
                ufa.europaLeague_score = dto.europaLeague_score
                ufa.europaLeague_answered = dto.europaLeague_answered
                ufa.europaChampionship_score = dto.europaChampionship_score
                ufa.europaChampionship_answered = dto.europaChampionship_answered
                ufa.championsLeague_score = dto.championsLeague_score
                ufa.championsLeague_answered = dto.championsLeague_answered
                scores.ufaScores = ufa
            }
            
            r.insertOrUpdate(scores)
        }
        realm.close()
    }

    fun deleteAll(check: Check, onError: ((Throwable) -> Unit)? = null) {
        val mRealm = Realm.getDefaultInstance()
        mRealm.executeTransactionAsync(Realm.Transaction { realm ->
            realm.where(FQ_Scores::class.java).findAll().deleteAllFromRealm()
            disableAllAnswers(realm, DB_France::class.java)
            disableAllAnswers(realm, DB_Germany::class.java)
            disableAllAnswers(realm, DB_Italy::class.java)
            disableAllAnswers(realm, DB_England::class.java)
            disableAllAnswers(realm, DB_Spain::class.java)
            disableAllAnswers(realm, DB_Super_Cup::class.java)
            disableAllAnswers(realm, DB_Europa_League::class.java)
            disableAllAnswers(realm, DB_Europ_Championship::class.java)
            disableAllAnswers(realm, DB_Champions_League::class.java)
            disableAllAnswers(realm, DB_World_Championship::class.java)
            disableAllAnswers(realm, DB_VS_Ronaldo_Messi::class.java)
            disableAllAnswers(realm, DB_VS_RealM_Barcelona::class.java)
            setDefaultAllScores(realm)
        }, Realm.Transaction.OnSuccess {
            mRealm.close()
            check.onCheck()
        }, Realm.Transaction.OnError { error ->
            Log.e(LOG_DB, "deleteAll: failed to reset local Realm data", error)
            if (!mRealm.isClosed) {
                mRealm.close()
            }
            onError?.invoke(error)
        })
    }
}
