package com.arthuralexandryan.footballquiz.domain.auth

import kotlin.test.Test
import kotlin.test.assertEquals

class AuthenticatedUserTest {
    @Test
    fun recipientNamePrefersDisplayName() {
        val user = AuthenticatedUser(
            id = "uid",
            email = "player@example.com",
            displayName = "Football Player"
        )

        assertEquals("Football Player", user.recipientName())
    }

    @Test
    fun recipientNameFallsBackToEmailThenGuest() {
        assertEquals("player", AuthenticatedUser("uid", email = "player@example.com").recipientName())
        assertEquals("Guest", AuthenticatedUser("uid").recipientName())
    }
}
