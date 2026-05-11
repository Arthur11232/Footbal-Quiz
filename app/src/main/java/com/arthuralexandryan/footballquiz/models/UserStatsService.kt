package com.arthuralexandryan.footballquiz.models

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UserStatsService private constructor() {

    companion object {
        @Volatile
        private var instance: UserStatsService? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: UserStatsService().also { instance = it }
        }
        
        private const val COLLECTION_USERS = "users"
    }

    private val db = FirebaseFirestore.getInstance()

    fun uploadStats(userId: String, stats: UserStatsDTO, onComplete: (Boolean) -> Unit) {
        db.collection(COLLECTION_USERS)
            .document(userId)
            .set(stats, SetOptions.merge())
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun downloadStats(userId: String, onComplete: (UserStatsDTO?) -> Unit) {
        db.collection(COLLECTION_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    try {
                        val stats = document.toObject(UserStatsDTO::class.java)
                        onComplete(stats)
                    } catch (e: Exception) {
                        onComplete(null)
                    }
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    fun deleteStats(userId: String, onComplete: (Boolean) -> Unit) {
        db.collection(COLLECTION_USERS)
            .document(userId)
            .delete()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
}
