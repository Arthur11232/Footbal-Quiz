package com.arthuralexandryan.footballquiz.di

import com.arthuralexandryan.footballquiz.data.source.local.GameStateLocalDataSource
import com.arthuralexandryan.footballquiz.data.source.local.PlayProgressLocalDataSource
import com.arthuralexandryan.footballquiz.data.source.local.RealmGameStateLocalDataSource
import com.arthuralexandryan.footballquiz.data.source.local.RealmPlayProgressLocalDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataSourceModule = module {
    single<GameStateLocalDataSource> { RealmGameStateLocalDataSource() }
    single<PlayProgressLocalDataSource> { RealmPlayProgressLocalDataSource(androidContext(), get()) }
}
