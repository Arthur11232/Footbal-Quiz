package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Intent
import com.arthuralexandryan.footballquiz.auth.AuthManager
import com.arthuralexandryan.footballquiz.data.mapper.toAuthenticatedUser
import com.arthuralexandryan.footballquiz.data.repository.AuthRepository
import com.arthuralexandryan.footballquiz.domain.auth.AuthSignInResult
import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepositoryImpl(
    private val authManager: AuthManager
) : AuthRepository {

    override fun getCurrentUser(): AuthenticatedUser? =
        authManager.getCurrentUser()?.toAuthenticatedUser()

    override fun isUserSignedIn(): Boolean = authManager.isUserSignedIn()

    override fun getSignInIntent(): Intent = authManager.getSignInIntent()

    override suspend fun signInWithFirebase(idToken: String): AuthSignInResult {
        return suspendCancellableCoroutine { continuation ->
            authManager.signInWithFirebase(idToken) { authResult ->
                continuation.resume(
                    AuthSignInResult(
                        success = authResult.success,
                        user = authResult.user?.toAuthenticatedUser(),
                        errorMessage = authResult.errorMessage
                    )
                )
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

    override suspend fun deleteCurrentUser(): Result<Unit> {
        val user = authManager.getCurrentUser()
            ?: return Result.failure(IllegalStateException("No signed-in user to delete"))
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
