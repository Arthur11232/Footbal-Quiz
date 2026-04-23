package com.arthuralexandryan.footballquiz.fragments

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.net.Uri
import android.app.Application
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.utils.Constants
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = StartPageFragmentTest.TestApplication::class)
class StartPageFragmentTest {

    private val appContext = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Before
    fun setUp() {
        PreferenceManager.getDefaultSharedPreferences(appContext).edit().clear().commit()
    }

    @Test
    fun `guest user sees sign in button and hidden profile card`() {
        val fragment = launchFragment()

        assertEquals(View.VISIBLE, fragment.requireView().findViewById<View>(R.id.sign_in_play).visibility)
        assertEquals(View.GONE, fragment.requireView().findViewById<View>(R.id.my_account).visibility)
    }

    @Test
    fun `continue button is disabled for first play`() {
        PreferenceManager.getDefaultSharedPreferences(appContext)
            .edit()
            .putBoolean("isFirstPlay", true)
            .commit()

        val fragment = launchFragment()
        val continueButton = fragment.requireView().findViewById<View>(R.id.tvContinue)

        assertFalse(continueButton.isEnabled)
        assertEquals(0.33f, continueButton.alpha)
    }

    @Test
    fun `continue button is enabled when game already started`() {
        PreferenceManager.getDefaultSharedPreferences(appContext)
            .edit()
            .putBoolean("isFirstPlay", false)
            .commit()

        val fragment = launchFragment()
        val continueButton = fragment.requireView().findViewById<View>(R.id.tvContinue)

        assertTrue(continueButton.isEnabled)
        assertEquals(1f, continueButton.alpha)
    }

    @Test
    fun `signed in user sees profile card and email`() {
        val fragment = launchFragment()
        val user = Mockito.mock(FirebaseUser::class.java)
        Mockito.`when`(user.email).thenReturn("player@test.com")

        invokeUpdateUi(fragment, user)

        assertEquals(View.GONE, fragment.requireView().findViewById<View>(R.id.sign_in_play).visibility)
        assertEquals(View.VISIBLE, fragment.requireView().findViewById<View>(R.id.my_account).visibility)
        assertEquals(
            "player@test.com",
            fragment.requireView().findViewById<android.widget.TextView>(R.id.my_account_email).text.toString()
        )
    }

    @Test
    fun `agreement text has clickable privacy and terms links`() {
        val fragment = launchFragment()
        val agreement = fragment.requireView().findViewById<android.widget.TextView>(R.id.agreement)
        val text = agreement.text as android.text.Spanned
        val spans = text.getSpans(0, text.length, ClickableSpan::class.java)

        assertEquals(2, spans.size)
        assertTrue(agreement.movementMethod is LinkMovementMethod)
    }

    @Test
    fun `privacy and terms clicks open external urls`() {
        val fragment = launchFragment()
        val activity = fragment.requireActivity()
        val agreement = fragment.requireView().findViewById<android.widget.TextView>(R.id.agreement)
        val text = agreement.text as android.text.Spanned
        val spans = text.getSpans(0, text.length, ClickableSpan::class.java)
            .sortedBy { text.getSpanStart(it) }

        addResolveInfo(fragment.getString(R.string.privacy_policy_url))
        addResolveInfo(fragment.getString(R.string.terms_and_conditions_url))

        spans[0].onClick(agreement)
        val firstIntent = shadowOf(activity).nextStartedActivity
        assertNotNull(firstIntent)
        assertEquals(fragment.getString(R.string.privacy_policy_url), firstIntent.dataString)

        spans[1].onClick(agreement)
        val secondIntent = shadowOf(activity).nextStartedActivity
        assertNotNull(secondIntent)
        assertEquals(fragment.getString(R.string.terms_and_conditions_url), secondIntent.dataString)
    }

    @Test
    fun `continue click navigates to choose screen when local data is already synced`() {
        PreferenceManager.getDefaultSharedPreferences(appContext)
            .edit()
            .putBoolean("isFirstPlay", false)
            .putBoolean(Constant.INIT_DB, true)
            .putString(Constants.Localization, "ru")
            .putString("db_lang", "ru")
            .commit()

        val fragment = launchFragment()
        val navController = TestNavHostController(appContext).apply {
            setGraph(R.navigation.nav_graph)
            setCurrentDestination(R.id.startPageFragment)
        }
        Navigation.setViewNavController(fragment.requireView(), navController)

        fragment.requireView().findViewById<View>(R.id.tvContinue).performClick()

        assertEquals(R.id.chooseGameFragment, navController.currentDestination?.id)
    }

    private fun launchFragment(): StartPageFragment {
        val controller = Robolectric.buildActivity(TestHostActivity::class.java).setup()
        val activity = controller.get()
        val fragment = StartPageFragment()

        activity.supportFragmentManager.beginTransaction()
            .replace(TestHostActivity.CONTAINER_ID, fragment)
            .commitNow()

        return fragment
    }

    private fun invokeUpdateUi(fragment: StartPageFragment, user: FirebaseUser?) {
        val method = StartPageFragment::class.java.getDeclaredMethod("updateUI", FirebaseUser::class.java)
        method.isAccessible = true
        method.invoke(fragment, user)
    }

    private fun addResolveInfo(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val resolveInfo = ResolveInfo().apply {
            activityInfo = ActivityInfo().apply {
                packageName = "com.arthuralexandryan.footballquiz.test"
                name = "BrowserActivity"
            }
        }
        shadowOf(appContext.packageManager).addResolveInfoForIntent(intent, resolveInfo)
    }

    class TestHostActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            setTheme(R.style.FootballQuiz)
            super.onCreate(savedInstanceState)
            setContentView(FrameLayout(this).apply { id = CONTAINER_ID })
        }

        companion object {
            const val CONTAINER_ID = 1001
        }
    }

    class TestApplication : Application() {
        override fun onCreate() {
            super.onCreate()
            FirebaseApp.initializeApp(this)
        }
    }
}
