package com.arthuralexandryan.footballquiz.domain.question

interface QuestionCatalogRepository {
    suspend fun getActiveContentVersion(appVersionCode: Long): ContentVersion
    suspend fun getQuestions(request: QuestionCatalogRequest): List<CatalogQuestion>
}
