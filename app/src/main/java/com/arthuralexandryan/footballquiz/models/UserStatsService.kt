package com.arthuralexandryan.footballquiz.models

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UserStatsService(
    private val db: FirebaseFirestore
) {

    companion object {
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_CERTIFICATES = "certificates"
    }

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

    fun uploadCertificates(
        userId: String,
        certificates: List<UserCertificateDTO>,
        onComplete: (Boolean) -> Unit
    ) {
        if (certificates.isEmpty()) {
            onComplete(true)
            return
        }

        val batch = db.batch()
        val userDocument = db.collection(COLLECTION_USERS).document(userId)
        certificates.forEach { certificate ->
            val certificateDocument = userDocument
                .collection(COLLECTION_CERTIFICATES)
                .document(certificate.stageId)
            batch.set(certificateDocument, certificate, SetOptions.merge())
        }
        batch.commit()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun downloadCertificates(
        userId: String,
        onComplete: (List<UserCertificateDTO>) -> Unit
    ) {
        db.collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_CERTIFICATES)
            .get()
            .addOnSuccessListener { snapshot ->
                val certificates = snapshot.documents.mapNotNull { document ->
                    try {
                        document.toObject(UserCertificateDTO::class.java)
                    } catch (_: Exception) {
                        null
                    }
                }
                onComplete(certificates)
            }
            .addOnFailureListener {
                onComplete(emptyList())
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

    fun deleteCertificates(userId: String, onComplete: (Boolean) -> Unit) {
        val certificatesCollection = db.collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_CERTIFICATES)

        certificatesCollection
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    onComplete(true)
                    return@addOnSuccessListener
                }

                val batch = db.batch()
                snapshot.documents.forEach { document ->
                    batch.delete(document.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        onComplete(true)
                    }
                    .addOnFailureListener {
                        onComplete(false)
                    }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
}
