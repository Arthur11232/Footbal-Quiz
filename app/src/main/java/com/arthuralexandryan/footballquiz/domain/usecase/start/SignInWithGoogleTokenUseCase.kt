package com.arthuralexandryan.footballquiz.domain.usecase.start

import com.arthuralexandryan.footballquiz.auth.AuthManager
import com.arthuralexandryan.footballquiz.data.repository.AuthRepository

class SignInWithGoogleTokenUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): AuthManager.AuthResult {
        return authRepository.signInWithFirebase(idToken)
    }
}
