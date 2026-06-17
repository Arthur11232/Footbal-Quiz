package com.arthuralexandryan.footballquiz.data.source.local

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
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.models.ScoreboardModel
import com.arthuralexandryan.footballquiz.utils.CategoryType
import com.arthuralexandryan.footballquiz.utils.Constants
import io.realm.Realm

class RealmGameCatalogLocalDataSource : GameCatalogLocalDataSource {

    override fun getTop5Answered(): Int = Realm.getDefaultInstance().use { realm ->
        realm.where(FQ_Scores::class.java).findFirst()?.top5_answered ?: 0
    }

    override fun getTop5AndUefaAnswered(): Int = Realm.getDefaultInstance().use { realm ->
        realm.where(FQ_Scores::class.java).findFirst()?.let { it.top5_answered + it.ufa_answered } ?: 0
    }

    override fun getTop5Scoreboards(): List<ScoreboardModel> = Realm.getDefaultInstance().use { realm ->
        val scores = realm.where(FQ_Scores::class.java).findFirst() ?: return@use emptyList()
        val top5 = scores.top5Scores
        listOf(
            ScoreboardModel(top5?.france_score ?: 40, top5?.france_answered ?: 0, scores.top5_answered, scores.top5),
            ScoreboardModel(top5?.germany_score ?: 40, top5?.germany_answered ?: 0, scores.top5_answered, scores.top5),
            ScoreboardModel(top5?.italy_score ?: 40, top5?.italy_answered ?: 0, scores.top5_answered, scores.top5),
            ScoreboardModel(top5?.england_score ?: 40, top5?.england_answered ?: 0, scores.top5_answered, scores.top5),
            ScoreboardModel(top5?.spain_score ?: 40, top5?.spain_answered ?: 0, scores.top5_answered, scores.top5)
        )
    }

    override fun getUefaScoreboards(): List<ScoreboardModel> = Realm.getDefaultInstance().use { realm ->
        val scores = realm.where(FQ_Scores::class.java).findFirst() ?: return@use emptyList()
        val ufa = scores.ufaScores
        listOf(
            ScoreboardModel(ufa?.superCup_score ?: 20, ufa?.superCup_answered ?: 0, scores.ufa_answered, scores.ufa),
            ScoreboardModel(ufa?.europaLeague_score ?: 20, ufa?.europaLeague_answered ?: 0, scores.ufa_answered, scores.ufa),
            ScoreboardModel(ufa?.championsLeague_score ?: 20, ufa?.championsLeague_answered ?: 0, scores.ufa_answered, scores.ufa),
            ScoreboardModel(ufa?.europaChampionship_score ?: 10, ufa?.europaChampionship_answered ?: 0, scores.ufa_answered, scores.ufa)
        )
    }

    override fun getWorldScoreboards(): List<ScoreboardModel> = Realm.getDefaultInstance().use { realm ->
        val scores = realm.where(FQ_Scores::class.java).findFirst() ?: return@use emptyList()
        listOf(ScoreboardModel(scores.world, scores.world_answered, scores.world_answered, 30))
    }

    override fun getRonMessiScoreboard(): ScoreboardModel = Realm.getDefaultInstance().use { realm ->
        val scores = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 50)
        ScoreboardModel(scores.vsRM, scores.vsRM_answered, scores.vsRM_answered, 50)
    }

    override fun getRealBarcaScoreboard(): ScoreboardModel = Realm.getDefaultInstance().use { realm ->
        val scores = realm.where(FQ_Scores::class.java).findFirst() ?: return@use ScoreboardModel(0, 0, 0, 50)
        ScoreboardModel(scores.vsRB, scores.vsRB_answered, scores.vsRB_answered, 50)
    }

    override fun getCategoryScore(categoryKey: String): GameObjectScores = Realm.getDefaultInstance().use { realm ->
        val fq = realm.where(FQ_Scores::class.java).findFirst() ?: return@use GameObjectScores()
        when (categoryKey) {
            "top5" -> GameObjectScores(fq.top5, fq.top5_answered, Constants.Top5)
            "uefa" -> GameObjectScores(fq.ufa, fq.ufa_answered, Constants.UFA)
            "world" -> GameObjectScores(fq.world, fq.world_answered, Constants.World)
            "vsRM" -> GameObjectScores(fq.vsRM, fq.vsRM_answered, Constants.VSRM)
            "vsRB" -> GameObjectScores(fq.vsRB, fq.vsRB_answered, Constants.VSRB)
            else -> GameObjectScores()
        }
    }

    override fun getLaunchData(entryKey: String): GameLaunchData = Realm.getDefaultInstance().use { realm ->
        when (entryKey) {
            "fransia" -> GameLaunchData(
                questions = realm.where(DB_France::class.java).findAll().map {
                    GameObjectSerializable(it.question, it.answer_A, it.answer_B, it.answer_C, it.answer_D, it.right_answer, it.isAnswered, "France", CategoryType.TOP5, it.questionId)
                },
                placeScores = getTop5PlaceScore(realm, entryKey)
            )
            "germania" -> GameLaunchData(
                questions = realm.where(DB_Germany::class.java).findAll().map {
                    GameObjectSerializable(it.question, it.answer_A, it.answer_B, it.answer_C, it.answer_D, it.right_answer, it.isAnswered, "Germany", CategoryType.TOP5, it.questionId)
                },
                placeScores = getTop5PlaceScore(realm, entryKey)
            )
            "italia" -> GameLaunchData(
                questions = realm.where(DB_Italy::class.java).findAll().map {
                    GameObjectSerializable(it.question, it.answer_A, it.answer_B, it.answer_C, it.answer_D, it.right_answer, it.isAnswered, "Italy", CategoryType.TOP5, it.questionId)
                },
                placeScores = getTop5PlaceScore(realm, entryKey)
            )
            "anglia" -> GameLaunchData(
                questions = realm.where(DB_England::class.java).findAll().map {
                    GameObjectSerializable(it.question, it.answer_A, it.answer_B, it.answer_C, it.answer_D, it.right_answer, it.isAnswered, "England", CategoryType.TOP5, it.questionId)
                },
                placeScores = getTop5PlaceScore(realm, entryKey)
            )
            "ispania" -> GameLaunchData(
                questions = realm.where(DB_Spain::class.java).findAll().map {
                    GameObjectSerializable(it.question, it.answer_A, it.answer_B, it.answer_C, it.answer_D, it.right_answer, it.isAnswered, "Spain", CategoryType.TOP5, it.questionId)
                },
                placeScores = getTop5PlaceScore(realm, entryKey)
            )
            "supercup" -> GameLaunchData(
                questions = realm.where(DB_Super_Cup::class.java).findAll().map {
                    GameObjectSerializable(it.question, it.answer_A, it.answer_B, it.answer_C, it.answer_D, it.right_answer, it.isAnswered, "Super Cup", CategoryType.UEFA, it.questionId)
                },
                placeScores = getUefaPlaceScore(realm, entryKey)
            )
            "evropaliga" -> GameLaunchData(
                questions = realm.where(DB_Europa_League::class.java).findAll().map {
                    GameObjectSerializable(it.question, it.answer_A, it.answer_B, it.answer_C, it.answer_D, it.right_answer, it.isAnswered, "European League", CategoryType.UEFA, it.questionId)
                },
                placeScores = getUefaPlaceScore(realm, entryKey)
            )
            "ligachemp" -> GameLaunchData(
                questions = realm.where(DB_Champions_League::class.java).findAll().map {
                    GameObjectSerializable(it.question, it.answer_A, it.answer_B, it.answer_C, it.answer_D, it.right_answer, it.isAnswered, "Champions League", CategoryType.UEFA, it.questionId)
                },
                placeScores = getUefaPlaceScore(realm, entryKey)
            )
            "kubki" -> GameLaunchData(
                questions = realm.where(DB_Europ_Championship::class.java).findAll().map {
                    GameObjectSerializable(it.question, it.answer_A, it.answer_B, it.answer_C, it.answer_D, it.right_answer, it.isAnswered, "Europe Championship", CategoryType.UEFA, it.questionId)
                },
                placeScores = getUefaPlaceScore(realm, entryKey)
            )
            "kubok_mira" -> GameLaunchData(
                questions = realm.where(DB_World_Championship::class.java).findAll().map {
                    GameObjectSerializable(it.question, it.answer_A, it.answer_B, it.answer_C, it.answer_D, it.right_answer, it.isAnswered, "World Cup", CategoryType.WORLD_CUP, it.questionId)
                },
                placeScores = getCategoryScore("world")
            )
            "vsRM" -> GameLaunchData(
                questions = realm.where(DB_VS_Ronaldo_Messi::class.java).findAll().map {
                    GameObjectSerializable(it.question, it.answer_A, it.answer_B, it.answer_C, it.answer_D, it.right_answer, it.isAnswered, "Versus R_M", CategoryType.RON_MES, it.questionId)
                },
                placeScores = getCategoryScore("vsRM")
            )
            "vsRB" -> GameLaunchData(
                questions = realm.where(DB_VS_RealM_Barcelona::class.java).findAll().map {
                    GameObjectSerializable(it.question, it.answer_A, it.answer_B, it.answer_C, it.answer_D, it.right_answer, it.isAnswered, "Versus R_B", CategoryType.REAL_BARCE, it.questionId)
                },
                placeScores = getCategoryScore("vsRB")
            )
            else -> GameLaunchData(emptyList(), GameObjectScores())
        }
    }

    private fun getTop5PlaceScore(realm: Realm, entryKey: String): GameObjectScores {
        val fq = realm.where(FQ_Scores::class.java).findFirst() ?: return GameObjectScores()
        val top5 = fq.top5Scores
        return when (entryKey) {
            "fransia" -> GameObjectScores(top5?.france_score ?: 40, top5?.france_answered ?: 0)
            "germania" -> GameObjectScores(top5?.germany_score ?: 40, top5?.germany_answered ?: 0)
            "italia" -> GameObjectScores(top5?.italy_score ?: 40, top5?.italy_answered ?: 0)
            "anglia" -> GameObjectScores(top5?.england_score ?: 40, top5?.england_answered ?: 0)
            "ispania" -> GameObjectScores(top5?.spain_score ?: 40, top5?.spain_answered ?: 0)
            else -> GameObjectScores()
        }
    }

    private fun getUefaPlaceScore(realm: Realm, entryKey: String): GameObjectScores {
        val fq = realm.where(FQ_Scores::class.java).findFirst() ?: return GameObjectScores()
        val ufa = fq.ufaScores
        return when (entryKey) {
            "supercup" -> GameObjectScores(ufa?.superCup_score ?: 20, ufa?.superCup_answered ?: 0)
            "evropaliga" -> GameObjectScores(ufa?.europaLeague_score ?: 20, ufa?.europaLeague_answered ?: 0)
            "ligachemp" -> GameObjectScores(ufa?.championsLeague_score ?: 20, ufa?.championsLeague_answered ?: 0)
            "kubki" -> GameObjectScores(ufa?.europaChampionship_score ?: 10, ufa?.europaChampionship_answered ?: 0)
            else -> GameObjectScores()
        }
    }
}
