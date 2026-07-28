package com.arthuralexandryan.footballquiz.di

import com.arthuralexandryan.footballquiz.presentation.play.PlayViewModel
import com.arthuralexandryan.footballquiz.presentation.profile.ProfileViewModel
import com.arthuralexandryan.footballquiz.presentation.start.StartPageViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { StartPageViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { PlayViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}
