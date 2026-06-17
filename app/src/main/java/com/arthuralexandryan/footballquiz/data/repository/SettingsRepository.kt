package com.arthuralexandryan.footballquiz.data.repository

interface SettingsRepository {
    fun isFirstPlay(): Boolean
    fun setFirstPlay(isFirstPlay: Boolean)

    fun isQuestionsInitialized(): Boolean
    fun setQuestionsInitialized(initialized: Boolean)

    fun getSelectedLocale(defaultValue: String = "en"): String
    fun setSelectedLocale(locale: String)

    fun getQuestionsLocale(): String?
    fun setQuestionsLocale(locale: String)

    fun getCheckedLanguage(defaultValue: Int): Int
    fun setCheckedLanguage(checkedLanguageId: Int)

    fun isAutoRestorePromptDismissed(userId: String): Boolean
    fun setAutoRestorePromptDismissed(userId: String, dismissed: Boolean)
}
