package com.arthuralexandryan.footballquiz.di

import com.arthuralexandryan.footballquiz.data.source.local.GameStateLocalDataSource
import com.arthuralexandryan.footballquiz.data.source.local.RealmGameStateLocalDataSource
import org.koin.dsl.module

val dataSourceModule = module {
    single<GameStateLocalDataSource> { RealmGameStateLocalDataSource() }
}
