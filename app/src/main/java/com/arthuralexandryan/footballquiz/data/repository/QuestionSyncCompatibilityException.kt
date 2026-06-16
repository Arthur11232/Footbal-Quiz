package com.arthuralexandryan.footballquiz.data.repository

class QuestionSyncCompatibilityException : IllegalStateException(
    "Localized question dataset is not ready for a safe questionId-based migration."
)
