package com.arthuralexandryan.footballquiz.domain.usecase.start

import com.arthuralexandryan.footballquiz.data.repository.SyncRepository
import com.google.firebase.auth.FirebaseUser

class UploadStartLocalStatsUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(user: FirebaseUser): Result<Unit> {
        return syncRepository.uploadLocalStats(user)
    }
}
