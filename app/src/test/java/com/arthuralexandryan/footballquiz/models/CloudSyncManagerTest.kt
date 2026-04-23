package com.arthuralexandryan.footballquiz.models

import android.app.Application
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.utils.Constants
import com.google.firebase.auth.FirebaseUser
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = CloudSyncManagerTest.TestApplication::class)
class CloudSyncManagerTest {

    private val appContext = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Before
    fun setUp() {
        PreferenceManager.getDefaultSharedPreferences(appContext).edit().clear().commit()
    }

    @After
    fun tearDown() {
        CloudSyncManager.downloadStatsAction =
            { userId, onComplete -> UserStatsService.getInstance().downloadStats(userId, onComplete) }
        CloudSyncManager.uploadStatsAction =
            { userId, stats, onComplete -> UserStatsService.getInstance().uploadStats(userId, stats, onComplete) }
    }

    @Test
    fun `resolveSyncDecision returns upload when cloud is empty and local has progress`() {
        val dbHelper = mockDbHelper(totalScore = 12, top5 = 3, uefa = 0, world = 0, versus = 0)
        val user = mockUser("user-1")
        CloudSyncManager.downloadStatsAction = { _, callback -> callback(null) }

        var result: CloudSyncManager.SyncDecision? = null
        CloudSyncManager.resolveSyncDecision(appContext, dbHelper, user) { result = it }

        assertTrue(result is CloudSyncManager.SyncDecision.UploadLocal)
    }

    @Test
    fun `resolveSyncDecision returns none when both local and cloud are empty`() {
        val dbHelper = mockDbHelper(totalScore = 0, top5 = 0, uefa = 0, world = 0, versus = 0)
        val user = mockUser("user-2")
        CloudSyncManager.downloadStatsAction = { _, callback -> callback(null) }

        var result: CloudSyncManager.SyncDecision? = null
        CloudSyncManager.resolveSyncDecision(appContext, dbHelper, user) { result = it }

        assertTrue(result is CloudSyncManager.SyncDecision.None)
    }

    @Test
    fun `resolveSyncDecision returns restore when local progress is empty and cloud has progress`() {
        val dbHelper = mockDbHelper(totalScore = 0, top5 = 0, uefa = 0, world = 0, versus = 0)
        val cloudStats = cloudStats(total = 55, lastSynced = 100L)
        val user = mockUser("user-3")
        CloudSyncManager.downloadStatsAction = { _, callback -> callback(cloudStats) }

        var result: CloudSyncManager.SyncDecision? = null
        CloudSyncManager.resolveSyncDecision(appContext, dbHelper, user) { result = it }

        assertTrue(result is CloudSyncManager.SyncDecision.RestoreCloud)
        assertEquals(55, (result as CloudSyncManager.SyncDecision.RestoreCloud).cloudStats.gameState.total)
    }

    @Test
    fun `resolveSyncDecision returns restore when cloud score is greater`() {
        val dbHelper = mockDbHelper(totalScore = 40, top5 = 20, uefa = 0, world = 0, versus = 0)
        val cloudStats = cloudStats(total = 60, lastSynced = 100L)
        val user = mockUser("user-4")
        CloudSyncManager.downloadStatsAction = { _, callback -> callback(cloudStats) }

        var result: CloudSyncManager.SyncDecision? = null
        CloudSyncManager.resolveSyncDecision(appContext, dbHelper, user) { result = it }

        assertTrue(result is CloudSyncManager.SyncDecision.RestoreCloud)
    }

    @Test
    fun `resolveSyncDecision returns upload when local score is greater`() {
        val dbHelper = mockDbHelper(totalScore = 70, top5 = 30, uefa = 0, world = 0, versus = 0)
        val cloudStats = cloudStats(total = 60, lastSynced = 100L)
        val user = mockUser("user-5")
        CloudSyncManager.downloadStatsAction = { _, callback -> callback(cloudStats) }

        var result: CloudSyncManager.SyncDecision? = null
        CloudSyncManager.resolveSyncDecision(appContext, dbHelper, user) { result = it }

        assertTrue(result is CloudSyncManager.SyncDecision.UploadLocal)
    }

    @Test
    fun `resolveSyncDecision returns restore when scores are equal and cloud timestamp is newer`() {
        PreferenceManager.getDefaultSharedPreferences(appContext)
            .edit()
            .putLong(Constants.UserStatsLastSyncKeyPrefix + "user-6", 100L)
            .commit()

        val dbHelper = mockDbHelper(totalScore = 60, top5 = 30, uefa = 0, world = 0, versus = 0)
        val cloudStats = cloudStats(total = 60, lastSynced = 200L)
        val user = mockUser("user-6")
        CloudSyncManager.downloadStatsAction = { _, callback -> callback(cloudStats) }

        var result: CloudSyncManager.SyncDecision? = null
        CloudSyncManager.resolveSyncDecision(appContext, dbHelper, user) { result = it }

        assertTrue(result is CloudSyncManager.SyncDecision.RestoreCloud)
    }

    @Test
    fun `resolveSyncDecision returns upload when scores are equal and local timestamp is newer`() {
        PreferenceManager.getDefaultSharedPreferences(appContext)
            .edit()
            .putLong(Constants.UserStatsLastSyncKeyPrefix + "user-7", 300L)
            .commit()

        val dbHelper = mockDbHelper(totalScore = 60, top5 = 30, uefa = 0, world = 0, versus = 0)
        val cloudStats = cloudStats(total = 60, lastSynced = 200L)
        val user = mockUser("user-7")
        CloudSyncManager.downloadStatsAction = { _, callback -> callback(cloudStats) }

        var result: CloudSyncManager.SyncDecision? = null
        CloudSyncManager.resolveSyncDecision(appContext, dbHelper, user) { result = it }

        assertTrue(result is CloudSyncManager.SyncDecision.UploadLocal)
    }

    @Test
    fun `resolveSyncDecision returns none when scores and timestamps are equal`() {
        PreferenceManager.getDefaultSharedPreferences(appContext)
            .edit()
            .putLong(Constants.UserStatsLastSyncKeyPrefix + "user-8", 200L)
            .commit()

        val dbHelper = mockDbHelper(totalScore = 60, top5 = 30, uefa = 0, world = 0, versus = 0)
        val cloudStats = cloudStats(total = 60, lastSynced = 200L)
        val user = mockUser("user-8")
        CloudSyncManager.downloadStatsAction = { _, callback -> callback(cloudStats) }

        var result: CloudSyncManager.SyncDecision? = null
        CloudSyncManager.resolveSyncDecision(appContext, dbHelper, user) { result = it }

        assertTrue(result is CloudSyncManager.SyncDecision.None)
    }

    @Test
    fun `restoreCloudStats updates prefs and delegates restore to db`() {
        val dbHelper = Mockito.mock(DB_Helper::class.java)
        val cloudStats = cloudStats(total = 88, lastSynced = 555L)

        CloudSyncManager.restoreCloudStats(appContext, dbHelper, "user-9", cloudStats)

        Mockito.verify(dbHelper).restoreStats(cloudStats.gameState)
        assertTrue(
            PreferenceManager.getDefaultSharedPreferences(appContext)
                .getBoolean("isFirstPlay", true)
                .not()
        )
        assertTrue(
            PreferenceManager.getDefaultSharedPreferences(appContext)
                .getBoolean(Constant.INIT_DB, false)
        )
        assertEquals(
            555L,
            PreferenceManager.getDefaultSharedPreferences(appContext)
                .getLong(Constants.UserStatsLastSyncKeyPrefix + "user-9", 0L)
        )
    }

    private fun mockDbHelper(
        totalScore: Int,
        top5: Int,
        uefa: Int,
        world: Int,
        versus: Int
    ): DB_Helper {
        val dbHelper = Mockito.mock(DB_Helper::class.java)
        Mockito.doReturn(totalScore).`when`(dbHelper).getTotalScore()
        Mockito.doReturn(top5).`when`(dbHelper).top5AnsweredScores
        Mockito.doReturn(uefa).`when`(dbHelper).getUFAAnsweredScores()
        Mockito.doReturn(world).`when`(dbHelper).getWorldAnsweredScores()
        Mockito.doReturn(versus).`when`(dbHelper).getVersusAnsweredScores()
        return dbHelper
    }

    private fun mockUser(userId: String): FirebaseUser {
        val user = Mockito.mock(FirebaseUser::class.java)
        Mockito.`when`(user.uid).thenReturn(userId)
        Mockito.`when`(user.email).thenReturn("$userId@test.com")
        return user
    }

    private fun cloudStats(total: Int, lastSynced: Long): UserStatsDTO =
        UserStatsDTO(
            email = "cloud@test.com",
            regId = "cloud-id",
            gameState = GameState(
                total = total,
                top5_answered = if (total > 0) 1 else 0,
                lastSynced = lastSynced
            )
        )

    class TestApplication : Application()
}
