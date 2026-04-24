package com.arthuralexandryan.footballquiz.models

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AllQuestions {
    fun size(): Int = list?.size ?: 0

    private object HOLDER {
        val INSTANCE = AllQuestions()
    }

    companion object {
        val instance: AllQuestions by lazy { HOLDER.INSTANCE }
    }

    var list: List<QuestionModel>? = null
}

class QuestionModel {
    var question = ""
    var translation = ""
    var answered = false
    var place = ""
    var answers: Answers? = null
}

class Answers {
    var A = ""
    var B = ""
    var C = ""
    var D = ""
    var right = ""
}

fun getQuestions(db: FirebaseFirestore, localize: String?, getQuest: (Boolean, List<QuestionModel>?) -> Unit) {
    db.collection("questions")
        .whereEqualTo("translation", localize)
        .get()
        .addOnCompleteListener {task ->
            task.result?.documents?.apply {
                if (task.isComplete && this.isNotEmpty()) {
                    task.result?.documents?.let { documents ->
                        GlobalScope.launch {
                            getQuest(true, handlerTask(documents))
                        }
                    }
                } else {
                    getQuest(false, null)
                }
            }

        }
}

fun handlerTask(document: MutableList<DocumentSnapshot>): List<QuestionModel> {
    return document.map<DocumentSnapshot, QuestionModel> {
        ObjectMapper()
            .convertValue<QuestionModel>(it.data, QuestionModel::class.java)
    }
}

enum class places(val place: String) {
    FRANCE("France"),
    GERMANY("Germany"),
    ITALY("Italy"),
    ENGLISH("English"),
    SPAIN("Spain"),
    SUPER_CUP("Super Cup"),
    EUROPA_LEAGUE("Europa League"),
    EUROPA_CHAMPIONS("\"European Championship"),
    CHAMPIONS("Champions League"),
    WORLD("World Championship"),
    RON_MESSI("vsRM"),
    REAL_BARCA("vsRB")
}

interface OnGetQuestions {
    fun isGotQuest(isGot: Boolean, questions: List<QuestionModel>?)
}

fun interface IsSetQuestions {
    fun finish()
}
