package com.arthuralexandryan.footballquiz.data.repository.impl

import android.content.Context
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.constants.Constant.INIT_DB
import com.arthuralexandryan.footballquiz.data.repository.SettingsRepository
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer
import java.util.UUID

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

    override fun getNotificationInstallationId(): String {
        val cached = Prefer.getStringPreference(context, Constants.NotificationInstallationIdKey, "")
            ?.takeIf { it.isNotBlank() }
        if (cached != null) return cached

        val generated = UUID.randomUUID().toString()
        Prefer.setStringPreference(context, Constants.NotificationInstallationIdKey, generated)
        return generated
    }

    override fun getCachedNotificationToken(): String? =
        Prefer.getStringPreference(context, Constants.NotificationCachedTokenKey, "")
            ?.takeIf { it.isNotBlank() }

    override fun setCachedNotificationToken(token: String?) {
        Prefer.setStringPreference(context, Constants.NotificationCachedTokenKey, token.orEmpty())
    }

    override fun getNotificationBoundUserId(): String? =
        Prefer.getStringPreference(context, Constants.NotificationBoundUserIdKey, "")
            ?.takeIf { it.isNotBlank() }

    override fun setNotificationBoundUserId(userId: String?) {
        Prefer.setStringPreference(context, Constants.NotificationBoundUserIdKey, userId.orEmpty())
    }

    override fun getNotificationLastSyncedAt(): Long =
        Prefer.getLongPreference(context, Constants.NotificationLastSyncedAtKey, 0L)

    override fun setNotificationLastSyncedAt(timestampMillis: Long) {
        Prefer.setLongPreference(context, Constants.NotificationLastSyncedAtKey, timestampMillis)
    }

    override fun areNotificationsEnabled(): Boolean =
        Prefer.getBooleanPreference(context, Constants.NotificationGlobalEnabledKey, true)

    override fun setNotificationsEnabled(enabled: Boolean) {
        Prefer.setBooleanPreference(context, Constants.NotificationGlobalEnabledKey, enabled)
    }

    override fun areProgressNotificationsEnabled(): Boolean =
        Prefer.getBooleanPreference(context, Constants.NotificationProgressEnabledKey, true)

    override fun setProgressNotificationsEnabled(enabled: Boolean) {
        Prefer.setBooleanPreference(context, Constants.NotificationProgressEnabledKey, enabled)
    }

    override fun areRewardNotificationsEnabled(): Boolean =
        Prefer.getBooleanPreference(context, Constants.NotificationRewardEnabledKey, true)

    override fun setRewardNotificationsEnabled(enabled: Boolean) {
        Prefer.setBooleanPreference(context, Constants.NotificationRewardEnabledKey, enabled)
    }

    override fun areUpdateNotificationsEnabled(): Boolean =
        Prefer.getBooleanPreference(context, Constants.NotificationUpdatesEnabledKey, true)

    override fun setUpdateNotificationsEnabled(enabled: Boolean) {
        Prefer.setBooleanPreference(context, Constants.NotificationUpdatesEnabledKey, enabled)
    }

    override fun isNotificationPermissionPromptShown(userId: String): Boolean =
        Prefer.getBooleanPreference(
            context,
            Constants.NotificationPromptShownKeyPrefix + userId,
            false
        )

    override fun setNotificationPermissionPromptShown(userId: String, shown: Boolean) {
        Prefer.setBooleanPreference(
            context,
            Constants.NotificationPromptShownKeyPrefix + userId,
            shown
        )
    }

    override fun getAppReviewPositiveAnswers(): Int =
        Prefer.getIntPreference(context, Constants.AppReviewPositiveAnswersKey, 0)

    override fun setAppReviewPositiveAnswers(count: Int) {
        Prefer.setIntPreference(context, Constants.AppReviewPositiveAnswersKey, count)
    }

    override fun getAppReviewPromptCount(): Int =
        Prefer.getIntPreference(context, Constants.AppReviewPromptCountKey, 0)

    override fun setAppReviewPromptCount(count: Int) {
        Prefer.setIntPreference(context, Constants.AppReviewPromptCountKey, count)
    }

    override fun getAppReviewLastPromptedAt(): Long =
        Prefer.getLongPreference(context, Constants.AppReviewLastPromptedAtKey, 0L)

    override fun setAppReviewLastPromptedAt(timestampMillis: Long) {
        Prefer.setLongPreference(context, Constants.AppReviewLastPromptedAtKey, timestampMillis)
    }
}
