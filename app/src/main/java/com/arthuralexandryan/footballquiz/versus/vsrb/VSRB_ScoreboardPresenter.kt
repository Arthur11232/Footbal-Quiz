package com.arthuralexandryan.footballquiz.versus.vsrb

import android.content.Context
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.db_app.Score.FQ_Scores
import com.arthuralexandryan.footballquiz.db_app.Versus.DB_VS_RealM_Barcelona
import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.utils.CategoryType
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.versus.AllCallBacks
import com.arthuralexandryan.footballquiz.versus.OnCheckScoreboard
import com.arthuralexandryan.footballquiz.versus.VS_ScoreboardModel
import io.realm.Realm

internal class VSRB_ScoreboardPresenter(
    private val context: Context,
    private val checkScoreboard: OnCheckScoreboard
) : AllCallBacks.Presenter {

    init {
        getScoreboardScores()
    }

    fun getTotalScores(): Int = DB_Helper().getTotalScore()
    fun getTotalScoresText(): String = DB_Helper().getTotalScoreText()
    fun isReadyToPlay(): Boolean = getTotalScores() >= 310

    private fun getScoreboardScores() {
        val dbHelper = DB_Helper()
        checkScoreboard.onCheckScores(
            VS_ScoreboardModel(
                dbHelper.realBarcScoreBoard.placeAnswered.toString(),
                dbHelper.realBarcScoreBoard.placeScore.toString(),
                dbHelper.realBarcScoreBoard.categoryScore.toString(),
                dbHelper.realBarcScoreBoard.categoryAnswered.toString()
            )
        )
    }

    private fun getItemScore(): GameObjectScores {
        val fq = Realm.getDefaultInstance().where(FQ_Scores::class.java).findFirst()!!
        return GameObjectScores(
            place_score = fq.vsRB,
            place_score_answer = fq.vsRB_answered,
            category_score = Constants.VSRB
        )
    }

    private fun getVersusQuestion(): List<GameObjectSerializable> =
        Realm.getDefaultInstance().where(DB_VS_RealM_Barcelona::class.java).findAll()
            .map { item ->
                GameObjectSerializable(
                    item.question, item.answer_A, item.answer_B, item.answer_C, item.answer_D,
                    item.right_answer, item.isAnswered, "Versus R_B", CategoryType.REAL_BARCE
                )
            }

    override fun onButtonClick() {}
}
