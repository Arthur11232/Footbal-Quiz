package com.arthuralexandryan.footballquiz.data.repository

import android.content.Intent
import com.arthuralexandryan.footballquiz.auth.AuthManager
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    fun getCurrentUser(): FirebaseUser?
    fun isUserSignedIn(): Boolean
    fun getSignInIntent(): Intent
    suspend fun signInWithFirebase(idToken: String): AuthManager.AuthResult
    fun signOut(onComplete: () -> Unit)
}
