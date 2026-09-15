package com.arthuralexandryan.footballquiz.domain.usecase.start

import com.arthuralexandryan.footballquiz.data.repository.AuthRepository
import com.arthuralexandryan.footballquiz.domain.auth.AuthSignInResult

class SignInWithGoogleTokenUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): AuthSignInResult {
        return authRepository.signInWithFirebase(idToken)
    }
}
