package com.arthuralexandryan.footballquiz.di

import android.app.Activity
import com.arthuralexandryan.footballquiz.auth.AuthManager
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.models.FirestoreQuestionService
import com.arthuralexandryan.footballquiz.models.UserStatsService
import com.arthuralexandryan.footballquiz.utils.CategoryType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
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
    factory(named("play_db_helper")) { (activity: Activity?, categoryType: CategoryType?, type: String?) ->
        DB_Helper(activity, categoryType, type)
    }
}

val footballQuizModules = listOf(
    firebaseModule,
    serviceModule
)
