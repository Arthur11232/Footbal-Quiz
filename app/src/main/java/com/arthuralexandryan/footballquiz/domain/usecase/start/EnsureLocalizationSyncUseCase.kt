package com.arthuralexandryan.footballquiz.domain.usecase.start

import com.arthuralexandryan.footballquiz.data.repository.QuestionsRepository

class EnsureLocalizationSyncUseCase(
    private val questionsRepository: QuestionsRepository
) {
    suspend operator fun invoke(isNew: Boolean): Result<Unit> {
        return questionsRepository.ensureLocalizationSync(isNew)
    }
}
