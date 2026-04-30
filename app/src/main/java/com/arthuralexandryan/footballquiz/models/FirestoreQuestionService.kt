package com.arthuralexandryan.footballquiz.models

import android.util.Log
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
                            val questionData = doc.data.orEmpty()
                            val place = questionData["place"] as? String ?: ""
                            val answers = questionData["answers"] as? Map<*, *>
                            GameObjectSerializable(
                                question = questionData["question"] as? String,
                                type = place,
                                categoryType = mapToCategoryType(place),
                                answer_A = answers?.get("a") as? String,
                                answer_B = answers?.get("b") as? String,
                                answer_C = answers?.get("c") as? String,
                                answer_D = answers?.get("d") as? String,
                                right_answer = answers?.get("right") as? String,
                                isAnswered = questionData["answered"] as? Boolean ?: false
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
