package com.arthuralexandryan.footballquiz.shared

import com.arthuralexandryan.footballquiz.domain.progression.ProgressionRules
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.domain.progression.StageProgress

class SharedDiagnostics {
    val moduleName: String = SharedBuildInfo.name
    val apiVersion: Int = 1
    val stageCount: Int = ProgressionStageId.entries.size
    val stageIdsText: String = ProgressionStageId.entries.joinToString("\n") { it.value }

    fun isFranceComplete(answered: Int): Boolean = ProgressionRules.evaluate(
        progress = listOf(StageProgress(ProgressionStageId.TOP5_FRANCE_COMPLETE, answered, 40)),
        completedStages = emptyList()
    ).stages.first { it.stageId == ProgressionStageId.TOP5_FRANCE_COMPLETE }.isCurrentlyCompleted
}
