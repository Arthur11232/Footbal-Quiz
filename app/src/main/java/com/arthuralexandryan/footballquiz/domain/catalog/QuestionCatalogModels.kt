package com.arthuralexandryan.footballquiz.domain.catalog

data class ContentVersion(
    val value: String
) {
    init {
        require(value.isNotBlank()) { "Content version must not be blank." }
    }
}

data class QuestionId(
    val value: String
) {
    init {
        require(value.isNotBlank()) { "Question id must not be blank." }
    }
}

data class QuestionCatalogFilter(
    val languageCode: String,
    val categoryKey: String? = null,
    val placeKey: String? = null
) {
    init {
        require(languageCode.isNotBlank()) { "Language code must not be blank." }
    }
}

data class CatalogQuestion(
    val id: QuestionId,
    val question: String,
    val answers: CatalogAnswers,
    val placeKey: String,
    val categoryKey: String
)

data class CatalogAnswers(
    val a: String,
    val b: String,
    val c: String,
    val d: String,
    val right: String
)

data class QuestionCatalogSnapshot(
    val contentVersion: ContentVersion,
    val languageCode: String,
    val questions: List<CatalogQuestion>
)
