package com.arthuralexandryan.footballquiz.domain.usecase.start

import com.arthuralexandryan.footballquiz.data.repository.SyncRepository
import com.arthuralexandryan.footballquiz.domain.auth.AuthenticatedUser

class ResolveStartCloudSyncUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(user: AuthenticatedUser): SyncRepository.SyncDecision {
        return syncRepository.resolveSyncDecision(user)
    }
}
