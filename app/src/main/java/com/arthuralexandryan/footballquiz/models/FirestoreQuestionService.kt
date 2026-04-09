package com.arthuralexandryan.footballquiz.models

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirestoreQuestionService {

    companion object {
        @Volatile
        private var instance: FirestoreQuestionService? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: FirestoreQuestionService().also { instance = it }
        }
    }

    private val db = FirebaseFirestore.getInstance()
    private val mapper = ObjectMapper()

    fun getQuestions(localize: String, callback: OnGetFirestoreQuestions) {
        Log.d("FQ_Log", "FirestoreQuestionService.getQuestions: locale=$localize")
        db.collection("questions")
            .whereEqualTo("translation", localize)
            .get()
            .addOnSuccessListener { result ->
                if (result != null && !result.isEmpty) {
                    Log.d("FQ_Log", "FirestoreQuestionService.getQuestions: success count=${result.size()} locale=$localize")
                    CoroutineScope(Dispatchers.IO).launch {
                        val questions = result.documents.map { doc ->
                            val qModel = mapper.convertValue(doc.data, FirestoreQuestionDTO::class.java)
                            GameObjectSerializable(
                                question = qModel.question,
                                type = qModel.place,
                                categoryType = mapToCategoryType(qModel.place),
                                answer_A = qModel.answers?.A,
                                answer_B = qModel.answers?.B,
                                answer_C = qModel.answers?.C,
                                answer_D = qModel.answers?.D,
                                right_answer = qModel.answers?.right,
                                isAnswered = qModel.answered
                            )
                        }
                        callback.onQuestionsLoaded(true, questions)
                    }
                } else {
                    Log.e("FQ_Log", "FirestoreQuestionService.getQuestions: empty result for locale=$localize")
                    callback.onQuestionsLoaded(false, null)
                }
            }
            .addOnFailureListener { error ->
                Log.e("FQ_Log", "FirestoreQuestionService.getQuestions: failed for locale=$localize", error)
                callback.onQuestionsLoaded(false, null)
            }
    }

    private fun mapToCategoryType(place: String): com.arthuralexandryan.footballquiz.utils.CategoryType? {
        return when (place) {
            "France", "Germany", "Italy", "English", "Spain" -> com.arthuralexandryan.footballquiz.utils.CategoryType.TOP5
            "Super Cup", "European League", "Champions League", "European Championship" -> com.arthuralexandryan.footballquiz.utils.CategoryType.UEFA
            "World Championship" -> com.arthuralexandryan.footballquiz.utils.CategoryType.WORLD_CUP
            "vsRM" -> com.arthuralexandryan.footballquiz.utils.CategoryType.RON_MES
            "vsRB" -> com.arthuralexandryan.footballquiz.utils.CategoryType.REAL_BARCE
            else -> null
        }
    }
}

fun interface OnGetFirestoreQuestions {
    fun onQuestionsLoaded(success: Boolean, questions: List<GameObjectSerializable>?)
}

data class FirestoreQuestionDTO(
    var question: String = "",
    var translation: String = "",
    var answered: Boolean = false,
    var place: String = "",
    var answers: FirestoreAnswersDTO? = null
)

data class FirestoreAnswersDTO(
    var A: String = "",
    var B: String = "",
    var C: String = "",
    var D: String = "",
    var right: String = ""
)
