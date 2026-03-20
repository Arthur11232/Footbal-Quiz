package com.arthuralexandryan.footballquiz.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameObjectParcelable(
    var question: String = "",
    var answer_A: String = "",
    var answer_B: String = "",
    var answer_C: String = "",
    var answer_D: String = "",
    var right_answer: String = "",
    var isAnswered: Boolean = false
) : Parcelable
