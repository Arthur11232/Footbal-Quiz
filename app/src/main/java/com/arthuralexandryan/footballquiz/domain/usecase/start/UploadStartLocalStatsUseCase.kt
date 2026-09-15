package com.arthuralexandryan.footballquiz.domain.usecase.start

import com.arthuralexandryan.footballquiz.data.repository.SyncRepository
import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser

class UploadStartLocalStatsUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(user: AuthenticatedUser): Result<Unit> {
        return syncRepository.uploadLocalStats(user)
    }
}
