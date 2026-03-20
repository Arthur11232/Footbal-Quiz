package com.arthuralexandryan.footballquiz

import android.app.Application
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.models.GetQuestions
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
        MobileAds.initialize(this, getString(R.string.AdUser))
        val realmConfiguration = RealmConfiguration.Builder()
            .name("FootballQuiz")
            .allowWritesOnUiThread(true)
             .schemaVersion(1)
            // .migration(DB_Migration())
            .build()
        Realm.getInstance(realmConfiguration)
        Realm.setDefaultConfiguration(realmConfiguration)
    }

    // override fun onTerminate() {
    //     Realm.getDefaultInstance().close()
    //     super.onTerminate()
    // }

    fun setDB(dbHelper: DB_Helper, questions: GetQuestions, isNew: Boolean) {
        dbHelper.setFranceQuestions(questions.getFrance().harcer)
        dbHelper.setGermanyQuestions(questions.getGermany().harcer)
        dbHelper.setItalyQuestions(questions.getItaly().harcer)
        dbHelper.setEnglishQuestions(questions.getEnglish().harcer)
        dbHelper.setSpainQuestions(questions.getSpain().harcer)
        dbHelper.setSuperCupQuestions(questions.getSuperCup().harcer)
        dbHelper.setEuropeanLeagueQuestions(questions.getEuropaLeague().harcer)
        dbHelper.setEuropeanChampeonQuestions(questions.getEuropeanChampionship().harcer)
        dbHelper.setChampionsQuestions(questions.getChampionsLeague().harcer)
        dbHelper.setWorldQuestions(questions.getWorldChampionship().harcer)
        dbHelper.setRMVersusQuestions(questions.getVSRM().harcer)
        dbHelper.setRBVersusQuestions(questions.getVSRB().harcer)
        if (isNew) {
            dbHelper.setDefaultAllScores()
        }
    }
}
