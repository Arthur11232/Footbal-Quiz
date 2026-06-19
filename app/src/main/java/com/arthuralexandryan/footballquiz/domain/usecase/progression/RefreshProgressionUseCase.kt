package com.arthuralexandryan.footballquiz.domain.usecase.progression

import com.arthuralexandryan.footballquiz.data.repository.ProgressionRepository
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionSnapshot

class RefreshProgressionUseCase(
    private val progressionRepository: ProgressionRepository
) {
    operator fun invoke(): ProgressionSnapshot = progressionRepository.refreshProgression()
}
