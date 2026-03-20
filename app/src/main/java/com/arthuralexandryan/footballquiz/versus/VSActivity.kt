package com.arthuralexandryan.footballquiz.versus

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.activities.BaseActivity
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.db_app.Score.FQ_Scores
import com.arthuralexandryan.footballquiz.db_app.Versus.DB_VS_RealM_Barcelona
import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.models.TarberakArrays
import com.arthuralexandryan.footballquiz.utils.CategoryType
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.views.CircleImageView
import io.realm.Realm

class VSActivity : BaseActivity() {

    private lateinit var gameScore: View
    private lateinit var placeAnswer: TextView
    private lateinit var placeQuestion: TextView
    private lateinit var categoryScore: TextView
    private lateinit var categoryAnswer: TextView
    private lateinit var place_name: TextView
    private lateinit var dbHelper: DB_Helper
    private lateinit var onBack: AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_versus)
        dbHelper = DB_Helper()
        onBack = findViewById(R.id.onBack)

        val real = findViewById<View>(R.id.real_madrid)
        val barce = findViewById<View>(R.id.barcelona)
        real.findViewById<CircleImageView>(R.id.vs_item).setImageResource(TarberakArrays.rb_image[0])
        barce.findViewById<CircleImageView>(R.id.vs_item).setImageResource(TarberakArrays.rb_image[1])

        gameScore = findViewById(R.id.scoreboard)
        initScoresViews()
        inflateView()
    }

    private fun inflateView() {
        val scoreboardModel = dbHelper.realBarcScoreBoard
        placeQuestion.text = scoreboardModel.placeScore.toString()
        placeAnswer.text = scoreboardModel.placeAnswered.toString()
        categoryAnswer.text = scoreboardModel.categoryAnswered.toString()
        categoryScore.text = scoreboardModel.categoryScore.toString()
        onBack.setOnClickListener { onBackPressed() }
    }

    private fun initScoresViews() {
        categoryScore = gameScore.findViewById(R.id.category_score)
        categoryAnswer = gameScore.findViewById(R.id.category_answers)
        placeAnswer = gameScore.findViewById(R.id.place_answer)
        placeQuestion = gameScore.findViewById(R.id.place_question)
        place_name = gameScore.findViewById(R.id.place_name_scores)
        place_name.text = getString(R.string.versus)
    }

    private fun getItemScore(): GameObjectScores {
        val realm = Realm.getDefaultInstance()
        val fq = realm.where(FQ_Scores::class.java).findFirst()!!
        return GameObjectScores(
            place_score = fq.vsRB,
            place_score_answer = fq.vsRB_answered,
            category_score = Constants.VSRB
        )
    }

    private fun getVersusQuestion(): List<GameObjectSerializable> {
        return Realm.getDefaultInstance()
            .where(DB_VS_RealM_Barcelona::class.java).findAll()
            .map { item ->
                GameObjectSerializable(
                    item.question, item.answer_A, item.answer_B, item.answer_C, item.answer_D,
                    item.right_answer, item.isAnswered, "Versus R_B", CategoryType.REAL_BARCE
                )
            }
    }
}
