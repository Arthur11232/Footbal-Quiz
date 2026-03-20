package com.arthuralexandryan.footballquiz.interfaces

import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable

interface ChooseGame {
    fun onChoose(question: List<GameObjectSerializable>, placeScores: GameObjectScores, position: Int)
}
