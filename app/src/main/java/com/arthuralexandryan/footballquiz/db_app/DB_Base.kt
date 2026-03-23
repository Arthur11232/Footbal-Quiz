package com.arthuralexandryan.footballquiz.db_app

interface DB_Base {
    var id: Int
    var question: String?
    var answer_A: String?
    var answer_B: String?
    var answer_C: String?
    var answer_D: String?
    var right_answer: String?
    var isAnswered: Boolean
}
