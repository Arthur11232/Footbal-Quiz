package com.arthuralexandryan.footballquiz.data.mapper

import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toAuthenticatedUser(): AuthenticatedUser =
    AuthenticatedUser(
        id = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl?.toString()
    )
