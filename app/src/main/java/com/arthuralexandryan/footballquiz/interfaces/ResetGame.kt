package com.arthuralexandryan.footballquiz.interfaces

import com.arthuralexandryan.footballquiz.models.ScoreboardModel

interface ResetGame {
    fun reset(placeScore: ScoreboardModel, isForce: Boolean)
}