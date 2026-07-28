package com.arthuralexandryan.footballquiz

import android.app.Application
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.db_app.DB_Migration
import com.arthuralexandryan.footballquiz.di.footballQuizModules
import com.arthuralexandryan.footballquiz.utils.ensureDefaultNotificationChannel
import com.google.android.gms.ads.MobileAds
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FQ_Application : Application() {

    companion object {
        private lateinit var instance: FQ_Application
        fun getInstance(): FQ_Application = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@FQ_Application)
            modules(footballQuizModules)
        }

        Realm.init(this)

        if (Constant.ADS_ENABLED) {
            MobileAds.initialize(this) {}
        }

        val realmConfiguration = RealmConfiguration.Builder()
            .name("FootballQuiz")
            .allowWritesOnUiThread(true)
            .schemaVersion(DB_Migration.CURRENT_SCHEMA_VERSION)
            .migration(DB_Migration())
            .build()
        Realm.getInstance(realmConfiguration)
        Realm.setDefaultConfiguration(realmConfiguration)

        ensureDefaultNotificationChannel(this)
    }
}
