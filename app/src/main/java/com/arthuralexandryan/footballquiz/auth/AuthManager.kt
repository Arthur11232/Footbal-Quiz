package com.arthuralexandryan.footballquiz.auth

import android.content.Context
import android.content.Intent
import com.arthuralexandryan.footballquiz.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthManager(private val context: Context) {

    data class AuthResult(
        val success: Boolean,
        val user: FirebaseUser? = null,
        val errorMessage: String? = null
    )

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    fun signOut(onComplete: () -> Unit) {
        auth.signOut()
        getSignInClient().signOut().addOnCompleteListener {
            onComplete()
        }
    }

    fun getSignInIntent(): Intent = getSignInClient().signInIntent

    fun signInWithFirebase(idToken: String, onComplete: (AuthResult) -> Unit) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(AuthResult(success = true, user = auth.currentUser))
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: task.exception?.message
                    onComplete(AuthResult(success = false, errorMessage = errorMessage))
                }
            }
    }
}
