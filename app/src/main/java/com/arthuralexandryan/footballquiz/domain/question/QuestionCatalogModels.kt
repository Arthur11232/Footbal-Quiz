package com.arthuralexandryan.footballquiz.domain.question

data class CatalogAnswer(
    val id: String,
    val text: String
)

data class CatalogQuestion(
    val questionId: String,
    val language: String,
    val categoryType: String,
    val placeType: String,
    val question: String,
    val answers: List<CatalogAnswer>,
    val correctAnswerId: String,
    val contentVersion: String,
    val isActive: Boolean
)

data class QuestionCatalogRequest(
    val language: String,
    val contentVersion: String? = null,
    val appVersionCode: Long? = null
)
