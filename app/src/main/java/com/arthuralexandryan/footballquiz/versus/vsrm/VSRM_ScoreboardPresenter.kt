package com.arthuralexandryan.footballquiz.versus.vsrm

import android.content.Context
import android.content.Intent
import android.util.Log
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.db_app.Score.FQ_Scores
import com.arthuralexandryan.footballquiz.db_app.Versus.DB_VS_Ronaldo_Messi
import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.utils.CategoryType
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.versus.AllCallBacks
import com.arthuralexandryan.footballquiz.versus.OnCheckScoreboard
import com.arthuralexandryan.footballquiz.versus.VS_ScoreboardModel
import io.realm.Realm
import java.io.Serializable

class VSRM_ScoreboardPresenter(val context: Context, private val checkScoreboard: OnCheckScoreboard) : AllCallBacks.Presenter {
    override fun onButtonClick() {
        toPlay()
    }

    init {
        getScoreboardScores()
    }


    fun getTotalScores(): Int {
        return DB_Helper().getTotalScore()
    }

    fun getTotalScoresText(): String {
        return DB_Helper().getTotalScoreText()
    }

    fun isReadyToPlay(): Boolean {
        return getTotalScores() >= 310
    }
    private fun getScoreboardScores() {
        val dbHelper = DB_Helper()
        checkScoreboard.onCheckScores(VS_ScoreboardModel(
                dbHelper.ronMessiScoreBoard.placeAnswered.toString(),
                dbHelper.ronMessiScoreBoard.placeScore.toString(),
                dbHelper.ronMessiScoreBoard.categoryScore.toString(),
                dbHelper.ronMessiScoreBoard.categoryAnswered.toString()))
    }

    private fun toPlay(){
        // Handled via CategoryPageFragment or should be handled via callback
        // For now, we'll just log it or use a callback if we want to support this legacy path
        Log.d("VSRM_Presenter", "toPlay requested. Should navigate via NavController.")
    }

    private fun getVersusQuestion(): List<GameObjectSerializable> {
        val gameObjects = java.util.ArrayList<GameObjectSerializable>()
        val db_vs_ron_messi = Realm.getDefaultInstance().where(DB_VS_Ronaldo_Messi::class.java).findAll()
        for (i in db_vs_ron_messi.indices) {
            gameObjects.add(GameObjectSerializable(
                    db_vs_ron_messi[i]!!.question,
                    db_vs_ron_messi[i]!!.answer_A,
                    db_vs_ron_messi[i]!!.answer_B,
                    db_vs_ron_messi[i]!!.answer_C,
                    db_vs_ron_messi[i]!!.answer_D,
                    db_vs_ron_messi[i]!!.right_answer,
                    db_vs_ron_messi[i]!!.isAnswered,
                    "Versus R_M",
                    CategoryType.RON_MES))
        }
        return gameObjects
    }

    private fun getItemScore(): GameObjectScores{
        val score = GameObjectScores()
        val realm = Realm.getDefaultInstance()

        score.place_score = realm.where(FQ_Scores::class.java).findFirst()!!.vsRM
        score.place_score_answer = realm.where(FQ_Scores::class.java).findFirst()!!.vsRM_answered
        score.category_score = Constants.VSRM
        return score
    }
}