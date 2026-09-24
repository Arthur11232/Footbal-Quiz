package com.arthuralexandryan.footballquiz.domain.progression

data class StageProgress(
    val stageId: ProgressionStageId,
    val answered: Int,
    val total: Int
)
