package com.arthuralexandryan.footballquiz.data.repository

import android.content.Intent
import com.arthuralexandryan.footballquiz.domain.auth.AuthSignInResult
import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser

interface AuthRepository {
    fun getCurrentUser(): AuthenticatedUser?
    fun isUserSignedIn(): Boolean
    fun getSignInIntent(): Intent
    suspend fun signInWithFirebase(idToken: String): AuthSignInResult
    fun signOut(onComplete: () -> Unit)
    suspend fun signOut()
    suspend fun deleteCurrentUser(): Result<Unit>
}
