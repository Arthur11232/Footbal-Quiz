package com.arthuralexandryan.footballquiz.di

import com.arthuralexandryan.footballquiz.data.repository.AuthRepository
import com.arthuralexandryan.footballquiz.data.repository.GameProgressRepository
import com.arthuralexandryan.footballquiz.data.repository.QuestionsRepository
import com.arthuralexandryan.footballquiz.data.repository.SettingsRepository
import com.arthuralexandryan.footballquiz.data.repository.SyncRepository
import com.arthuralexandryan.footballquiz.data.repository.UserStatsRepository
import com.arthuralexandryan.footballquiz.data.repository.impl.AuthRepositoryImpl
import com.arthuralexandryan.footballquiz.data.repository.impl.GameProgressRepositoryImpl
import com.arthuralexandryan.footballquiz.data.repository.impl.QuestionsRepositoryImpl
import com.arthuralexandryan.footballquiz.data.repository.impl.SettingsRepositoryImpl
import com.arthuralexandryan.footballquiz.data.repository.impl.SyncRepositoryImpl
import com.arthuralexandryan.footballquiz.data.repository.impl.UserStatsRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl(androidContext()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<QuestionsRepository> { QuestionsRepositoryImpl(get(), get(), get()) }
    single<SyncRepository> { SyncRepositoryImpl(androidContext(), get(), get()) }
    single<UserStatsRepository> { UserStatsRepositoryImpl(get()) }
    single<GameProgressRepository> { GameProgressRepositoryImpl(androidContext(), get(), get()) }
}
