package com.arthuralexandryan.footballquiz.presentation.start

import com.arthuralexandryan.footballquiz.R

data class StartPageUiState(
    val isLoading: Boolean = false,
    val loadingMessageResId: Int = R.string.loading,
    val isContinueEnabled: Boolean = false,
    val isNewGame: Boolean = true,
    val isSignedIn: Boolean = false,
    val accountEmail: String? = null
)
