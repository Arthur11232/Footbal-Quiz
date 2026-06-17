package com.arthuralexandryan.footballquiz.di

import com.arthuralexandryan.footballquiz.auth.AuthManager
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.models.FirestoreQuestionService
import com.arthuralexandryan.footballquiz.models.UserStatsService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val firebaseModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
}

val serviceModule = module {
    single { FirestoreQuestionService(get()) }
    single { UserStatsService(get()) }
    factory { AuthManager(androidContext(), get()) }
    factory { DB_Helper() }
}

val footballQuizModules = listOf(
    firebaseModule,
    serviceModule,
    dataSourceModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
