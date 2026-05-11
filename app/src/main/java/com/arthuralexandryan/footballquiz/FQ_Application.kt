package com.arthuralexandryan.footballquiz

import android.app.Application
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.google.android.gms.ads.MobileAds
import io.realm.Realm
import io.realm.RealmConfiguration

class FQ_Application : Application() {

    companion object {
        private lateinit var instance: FQ_Application
        fun getInstance(): FQ_Application = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Realm.init(this)

        if (Constant.ADS_ENABLED) {
            MobileAds.initialize(this) {}
        }

        val realmConfiguration = RealmConfiguration.Builder()
            .name("FootballQuiz")
            .allowWritesOnUiThread(true)
            .schemaVersion(1)
            .build()
        Realm.getInstance(realmConfiguration)
        Realm.setDefaultConfiguration(realmConfiguration)
    }

    fun setDB(dbHelper: DB_Helper, isNew: Boolean) {
        // Migration: Firestore is now the source of truth, 
        // so legacy synchronous local json loading is deprecated.
    }
}
