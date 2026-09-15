package com.arthuralexandryan.footballquiz.domain.auth

data class AuthSignInResult(
    val success: Boolean,
    val user: AuthenticatedUser? = null,
    val errorMessage: String? = null
)
