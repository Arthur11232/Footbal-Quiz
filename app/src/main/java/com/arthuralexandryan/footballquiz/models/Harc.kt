package com.arthuralexandryan.footballquiz.models

data class Harc(
    var harc: String = "",
    var chist: String = "",
    var patasxanner: Patasxanner = Patasxanner(),
    var isAnswered: Boolean = false
)
