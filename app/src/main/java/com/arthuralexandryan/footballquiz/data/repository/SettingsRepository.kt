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

    fun isAutoRestorePromptCompleted(userId: String): Boolean
    fun setAutoRestorePromptCompleted(userId: String, completed: Boolean)

    fun getNotificationInstallationId(): String

    fun getCachedNotificationToken(): String?
    fun setCachedNotificationToken(token: String?)

    fun getNotificationBoundUserId(): String?
    fun setNotificationBoundUserId(userId: String?)

    fun getNotificationLastSyncedAt(): Long
    fun setNotificationLastSyncedAt(timestampMillis: Long)

    fun areNotificationsEnabled(): Boolean
    fun setNotificationsEnabled(enabled: Boolean)

    fun areProgressNotificationsEnabled(): Boolean
    fun setProgressNotificationsEnabled(enabled: Boolean)

    fun areRewardNotificationsEnabled(): Boolean
    fun setRewardNotificationsEnabled(enabled: Boolean)

    fun areUpdateNotificationsEnabled(): Boolean
    fun setUpdateNotificationsEnabled(enabled: Boolean)

    fun isNotificationPermissionPromptShown(userId: String): Boolean
    fun setNotificationPermissionPromptShown(userId: String, shown: Boolean)

    fun getAppReviewPositiveAnswers(): Int
    fun setAppReviewPositiveAnswers(count: Int)

    fun getAppReviewPromptCount(): Int
    fun setAppReviewPromptCount(count: Int)

    fun getAppReviewLastPromptedAt(): Long
    fun setAppReviewLastPromptedAt(timestampMillis: Long)
}
