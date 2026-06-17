package com.arthuralexandryan.footballquiz.data.repository

interface QuestionsRepository {
    suspend fun ensureLocalizationSync(isNew: Boolean): Result<Unit>
}
