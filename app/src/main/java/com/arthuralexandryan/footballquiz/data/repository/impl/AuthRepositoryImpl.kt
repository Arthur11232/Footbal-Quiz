package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Intent
import com.arthuralexandryan.footballquiz.auth.AuthManager
import com.arthuralexandryan.footballquiz.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepositoryImpl(
    private val authManager: AuthManager
) : AuthRepository {

    override fun getCurrentUser(): FirebaseUser? = authManager.getCurrentUser()

    override fun isUserSignedIn(): Boolean = authManager.isUserSignedIn()

    override fun getSignInIntent(): Intent = authManager.getSignInIntent()

    override suspend fun signInWithFirebase(idToken: String): AuthManager.AuthResult {
        return suspendCancellableCoroutine { continuation ->
            authManager.signInWithFirebase(idToken) { authResult ->
                continuation.resume(authResult)
            }
        }
    }

    override fun signOut(onComplete: () -> Unit) {
        authManager.signOut(onComplete)
    }

    override suspend fun signOut() {
        return suspendCancellableCoroutine { continuation ->
            authManager.signOut {
                continuation.resume(Unit)
            }
        }
    }

    override suspend fun deleteUser(user: FirebaseUser): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(task.exception ?: IllegalStateException("Delete user failed")))
                }
            }
        }
    }
}
