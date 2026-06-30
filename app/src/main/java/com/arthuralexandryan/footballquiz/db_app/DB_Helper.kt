package com.arthuralexandryan.footballquiz.db_app

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
import com.arthuralexandryan.footballquiz.models.Harc
import com.arthuralexandryan.footballquiz.models.IsSetQuestions
import com.arthuralexandryan.footballquiz.models.QuestionModel
import com.arthuralexandryan.footballquiz.models.places
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel

class DB_Helper {
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
        DB_France().apply { id = i; questionId = m.questionId; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: ""; isAnswered = m.answered }
    }
    fun setGermanyQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Germany().apply { id = i; questionId = m.questionId; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: ""; isAnswered = m.answered }
    }
    fun setItalyQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Italy().apply { id = i; questionId = m.questionId; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: ""; isAnswered = m.answered }
    }
    fun setEnglishQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_England().apply { id = i; questionId = m.questionId; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: ""; isAnswered = m.answered }
    }
    fun setSpainQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Spain().apply { id = i; questionId = m.questionId; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: ""; isAnswered = m.answered }
    }
    fun setSuperCupQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Super_Cup().apply { id = i; questionId = m.questionId; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: ""; isAnswered = m.answered }
    }
    fun setEuropeanLeagueQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Europa_League().apply { id = i; questionId = m.questionId; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: ""; isAnswered = m.answered }
    }
    fun setEuropeanChampeonQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Europ_Championship().apply { id = i; questionId = m.questionId; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: ""; isAnswered = m.answered }
    }
    fun setChampionsQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_Champions_League().apply { id = i; questionId = m.questionId; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: ""; isAnswered = m.answered }
    }
    fun setWorldQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_World_Championship().apply { id = i; questionId = m.questionId; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: ""; isAnswered = m.answered }
    }
    fun setRMVersusQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_VS_Ronaldo_Messi().apply { id = i; questionId = m.questionId; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: ""; isAnswered = m.answered }
    }
    fun setRBVersusQuestions(realm: Realm, modelList: List<QuestionModel>) = setQuestions(realm, modelList) { i, m ->
        DB_VS_RealM_Barcelona().apply { id = i; questionId = m.questionId; answer_A = m.answers?.A ?: ""; answer_B = m.answers?.B ?: ""; answer_C = m.answers?.C ?: ""; answer_D = m.answers?.D ?: ""; question = m.question; right_answer = m.answers?.right ?: ""; isAnswered = m.answered }
    }

    fun getAnsweredQuestionIds(): Set<String> {
        return Realm.getDefaultInstance().use { realm ->
            buildSet {
                addAll(collectAnsweredQuestionIds(realm, DB_France::class.java))
                addAll(collectAnsweredQuestionIds(realm, DB_Germany::class.java))
                addAll(collectAnsweredQuestionIds(realm, DB_Italy::class.java))
                addAll(collectAnsweredQuestionIds(realm, DB_England::class.java))
                addAll(collectAnsweredQuestionIds(realm, DB_Spain::class.java))
                addAll(collectAnsweredQuestionIds(realm, DB_Super_Cup::class.java))
                addAll(collectAnsweredQuestionIds(realm, DB_Europa_League::class.java))
                addAll(collectAnsweredQuestionIds(realm, DB_Europ_Championship::class.java))
                addAll(collectAnsweredQuestionIds(realm, DB_Champions_League::class.java))
                addAll(collectAnsweredQuestionIds(realm, DB_World_Championship::class.java))
                addAll(collectAnsweredQuestionIds(realm, DB_VS_Ronaldo_Messi::class.java))
                addAll(collectAnsweredQuestionIds(realm, DB_VS_RealM_Barcelona::class.java))
            }
        }
    }

    fun hasAnsweredQuestions(): Boolean {
        return Realm.getDefaultInstance().use { realm ->
            hasAnsweredQuestions(realm, DB_France::class.java) ||
                hasAnsweredQuestions(realm, DB_Germany::class.java) ||
                hasAnsweredQuestions(realm, DB_Italy::class.java) ||
                hasAnsweredQuestions(realm, DB_England::class.java) ||
                hasAnsweredQuestions(realm, DB_Spain::class.java) ||
                hasAnsweredQuestions(realm, DB_Super_Cup::class.java) ||
                hasAnsweredQuestions(realm, DB_Europa_League::class.java) ||
                hasAnsweredQuestions(realm, DB_Europ_Championship::class.java) ||
                hasAnsweredQuestions(realm, DB_Champions_League::class.java) ||
                hasAnsweredQuestions(realm, DB_World_Championship::class.java) ||
                hasAnsweredQuestions(realm, DB_VS_Ronaldo_Messi::class.java) ||
                hasAnsweredQuestions(realm, DB_VS_RealM_Barcelona::class.java)
        }
    }

    fun hasEmptyQuestionBuckets(): Boolean {
        return Realm.getDefaultInstance().use { realm ->
            hasNoQuestions(realm, DB_France::class.java) ||
                hasNoQuestions(realm, DB_Germany::class.java) ||
                hasNoQuestions(realm, DB_Italy::class.java) ||
                hasNoQuestions(realm, DB_England::class.java) ||
                hasNoQuestions(realm, DB_Spain::class.java) ||
                hasNoQuestions(realm, DB_Super_Cup::class.java) ||
                hasNoQuestions(realm, DB_Europa_League::class.java) ||
                hasNoQuestions(realm, DB_Europ_Championship::class.java) ||
                hasNoQuestions(realm, DB_Champions_League::class.java) ||
                hasNoQuestions(realm, DB_World_Championship::class.java) ||
                hasNoQuestions(realm, DB_VS_Ronaldo_Messi::class.java) ||
                hasNoQuestions(realm, DB_VS_RealM_Barcelona::class.java)
        }
    }

    private fun setDefaultAllScores(realm: Realm) {
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

    private fun <T> collectAnsweredQuestionIds(realm: Realm, modelClass: Class<T>): Set<String>
        where T : RealmModel, T : DB_Base {
        return realm.where(modelClass)
            .equalTo("isAnswered", true)
            .findAll()
            .mapNotNull { it.questionId?.takeIf(String::isNotBlank) }
            .toSet()
    }

    private fun <T : RealmModel> hasAnsweredQuestions(realm: Realm, modelClass: Class<T>): Boolean {
        return realm.where(modelClass)
            .equalTo("isAnswered", true)
            .count() > 0
    }

    private fun <T : RealmModel> hasNoQuestions(realm: Realm, modelClass: Class<T>): Boolean {
        return realm.where(modelClass)
            .count() == 0L
    }
}
