package com.arthuralexandryan.footballquiz.domain.usecase.start

import com.arthuralexandryan.footballquiz.data.repository.SyncRepository
import com.arthuralexandryan.footballquiz.models.UserStatsDTO

class RestoreStartCloudStatsUseCase(
    private val syncRepository: SyncRepository
) {
    operator fun invoke(userId: String, cloudStats: UserStatsDTO) {
        syncRepository.restoreCloudStats(userId, cloudStats)
    }
}
