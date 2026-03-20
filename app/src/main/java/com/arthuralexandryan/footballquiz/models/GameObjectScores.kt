package com.arthuralexandryan.footballquiz.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameObjectScores(
    var place_score: Int = 0,
    var place_score_answer: Int = 0,
    var category_score: String? = null
) : Parcelable {
    val placeScoreText: String
        get() = place_score.toString()

    val placeScoreAnswerText: String
        get() = place_score_answer.toString()
}
