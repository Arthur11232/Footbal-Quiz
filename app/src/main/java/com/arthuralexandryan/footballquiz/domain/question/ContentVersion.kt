package com.arthuralexandryan.footballquiz.domain.question

data class ContentVersion(
    val value: String,
    val minAppVersionCode: Long,
    val isActive: Boolean,
    val publishedAtMillis: Long
)
