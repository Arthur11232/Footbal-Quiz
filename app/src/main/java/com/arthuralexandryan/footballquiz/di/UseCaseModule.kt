package com.arthuralexandryan.footballquiz.di

import com.arthuralexandryan.footballquiz.domain.usecase.progression.RefreshProgressionUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.BuildStartPageStateUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.EnsureLocalizationSyncUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.ResolveStartCloudSyncUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.RestoreStartCloudStatsUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.SignInWithGoogleTokenUseCase
import com.arthuralexandryan.footballquiz.domain.usecase.start.UploadStartLocalStatsUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { RefreshProgressionUseCase(get()) }
    factory { BuildStartPageStateUseCase(get()) }
    factory { EnsureLocalizationSyncUseCase(get()) }
    factory { SignInWithGoogleTokenUseCase(get()) }
    factory { ResolveStartCloudSyncUseCase(get()) }
    factory { UploadStartLocalStatsUseCase(get()) }
    factory { RestoreStartCloudStatsUseCase(get()) }
}
