package com.arthuralexandryan.footballquiz.domain.catalog

interface QuestionCatalogRepository {
    suspend fun getActiveContentVersion(): ContentVersion

    suspend fun getQuestions(filter: QuestionCatalogFilter): QuestionCatalogSnapshot
}
