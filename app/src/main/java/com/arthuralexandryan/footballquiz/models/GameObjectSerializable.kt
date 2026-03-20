package com.arthuralexandryan.footballquiz.models

import android.os.Parcelable
import com.arthuralexandryan.footballquiz.utils.CategoryType
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameObjectSerializable(
    val question: String? = null,
    val answer_A: String? = null,
    val answer_B: String? = null,
    val answer_C: String? = null,
    val answer_D: String? = null,
    val right_answer: String? = null,
    var isAnswered: Boolean = false,
    val type: String? = null,
    val categoryType: CategoryType? = null
) : Parcelable
