package com.arthuralexandryan.footballquiz.models

class CategoryScoreObject {
    var categoryCurrentScore: Int = 0
    var categoryAllScore: Int = 0
    fun getCategoryCurrentScoreText(): String{
        return categoryCurrentScore.toString()
    }
    fun getCategoryAllScoreText(): String{
        return categoryAllScore.toString()
    }
}