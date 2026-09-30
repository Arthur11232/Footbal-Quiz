package com.arthuralexandryan.footballquiz.domain.auth

data class AuthenticatedUser(
    val id: String,
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null
) {
    fun recipientName(fallback: String = "Guest"): String {
        return displayName
            ?.takeIf { it.isNotBlank() }
            ?: email
                ?.substringBefore("@")
                ?.takeIf { it.isNotBlank() }
            ?: fallback
    }
}
