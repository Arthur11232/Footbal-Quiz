package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.constants.Constant.INIT_DB
import com.arthuralexandryan.footballquiz.data.repository.SettingsRepository
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer

class SettingsRepositoryImpl(
    private val context: Context
) : SettingsRepository {

    override fun isFirstPlay(): Boolean =
        Prefer.getBooleanPreference(context, "isFirstPlay", true)

    override fun setFirstPlay(isFirstPlay: Boolean) {
        Prefer.setBooleanPreference(context, "isFirstPlay", isFirstPlay)
    }

    override fun isQuestionsInitialized(): Boolean =
        Prefer.getBooleanPreference(context, INIT_DB, false)

    override fun setQuestionsInitialized(initialized: Boolean) {
        Prefer.setBooleanPreference(context, INIT_DB, initialized)
    }

    override fun getSelectedLocale(defaultValue: String): String =
        Prefer.getStringPreference(context, Constants.Localization, defaultValue) ?: defaultValue

    override fun setSelectedLocale(locale: String) {
        Prefer.setStringPreference(context, Constants.Localization, locale)
    }

    override fun getQuestionsLocale(): String? =
        Prefer.getStringPreference(context, "db_lang", "")

    override fun setQuestionsLocale(locale: String) {
        Prefer.setStringPreference(context, "db_lang", locale)
    }

    override fun getCheckedLanguage(defaultValue: Int): Int =
        Prefer.getIntPreference(context, Constants.CheckedLanguage, defaultValue)

    override fun setCheckedLanguage(checkedLanguageId: Int) {
        Prefer.setIntPreference(context, Constants.CheckedLanguage, checkedLanguageId)
    }

    override fun isAutoRestorePromptDismissed(userId: String): Boolean =
        Prefer.getBooleanPreference(
            context,
            Constants.UserStatsRestorePromptDismissedKeyPrefix + userId,
            false
        )

    override fun setAutoRestorePromptDismissed(userId: String, dismissed: Boolean) {
        Prefer.setBooleanPreference(
            context,
            Constants.UserStatsRestorePromptDismissedKeyPrefix + userId,
            dismissed
        )
    }
}
