package com.arthuralexandryan.footballquiz.domain.usecase.start

import com.arthuralexandryan.footballquiz.data.repository.SyncRepository
import com.arthuralexandryan.footballquiz.domain.sync.CloudProgressSnapshot

class RestoreStartCloudStatsUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(userId: String, cloudProgress: CloudProgressSnapshot) {
        syncRepository.restoreCloudProgress(userId, cloudProgress)
    }
}
