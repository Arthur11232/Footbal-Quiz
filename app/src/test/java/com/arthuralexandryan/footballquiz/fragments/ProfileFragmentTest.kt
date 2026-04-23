package com.arthuralexandryan.footballquiz.fragments

import android.app.Application
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.arthuralexandryan.footballquiz.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = ProfileFragmentTest.TestApplication::class)
class ProfileFragmentTest {

    private val appContext = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun `guest sees guest info and hidden account actions`() {
        val fragment = launchFragment(currentUser = null)

        assertEquals(appContext.getString(R.string.profile_guest), textOf(fragment, R.id.userName))
        assertEquals(appContext.getString(R.string.profile_not_signed_in), textOf(fragment, R.id.userEmail))
        assertEquals(View.GONE, view(fragment, R.id.btnSignOut).visibility)
        assertEquals(View.GONE, view(fragment, R.id.btnEditPhoto).visibility)
        assertEquals(View.GONE, view(fragment, R.id.btnRestoreCloud).visibility)
        assertEquals(View.GONE, view(fragment, R.id.btnOverwriteCloud).visibility)
        assertEquals(View.GONE, view(fragment, R.id.btnDeleteAccount).visibility)
    }

    @Test
    fun `signed in user sees email and cloud actions`() {
        val user = mockUser(
            uid = "u1",
            name = "Arthur",
            email = "arthur@test.com"
        )
        val fragment = launchFragment(currentUser = user)

        assertEquals("Arthur", textOf(fragment, R.id.userName))
        assertEquals("arthur@test.com", textOf(fragment, R.id.userEmail))
        assertEquals(View.VISIBLE, view(fragment, R.id.btnSignOut).visibility)
        assertEquals(View.VISIBLE, view(fragment, R.id.btnEditPhoto).visibility)
        assertEquals(View.VISIBLE, view(fragment, R.id.btnRestoreCloud).visibility)
        assertEquals(View.VISIBLE, view(fragment, R.id.btnOverwriteCloud).visibility)
        assertEquals(View.VISIBLE, view(fragment, R.id.btnDeleteAccount).visibility)
    }

    @Test
    fun `statistics are rendered from provided values`() {
        val fragment = launchFragment(
            currentUser = null,
            totalScore = 999,
            top5Answered = 10,
            uefaAnswered = 20,
            worldAnswered = 5,
            versusAnswered = 7
        )

        assertEquals("10%", textOf(fragment, R.id.tvTotalScoreValue))
        assertEquals("42/400", textOf(fragment, R.id.tvAnsweredCountValue))
        assertEquals("10/200", textOf(fragment, R.id.tvTop5Progress))
        assertEquals("20/70", textOf(fragment, R.id.tvUefaProgress))
        assertEquals("5/30", textOf(fragment, R.id.tvWorldProgress))
        assertEquals("7/100", textOf(fragment, R.id.tvVersusProgress))
    }

    @Test
    fun `sign out hides account actions and returns to start page`() {
        val signedInUser = mockUser(
            uid = "u2",
            name = "Player",
            email = "player@test.com"
        )
        var currentUser: FirebaseUser? = signedInUser

        val fragment = launchFragment(
            currentUser = signedInUser,
            currentUserProviderOverride = { currentUser },
            signOutActionOverride = { onComplete ->
                currentUser = null
                onComplete()
            }
        )

        val navController = TestNavHostController(appContext).apply {
            setGraph(R.navigation.nav_graph)
            setCurrentDestination(R.id.startPageFragment)
            navigate(R.id.action_start_to_profile)
        }
        Navigation.setViewNavController(fragment.requireView(), navController)

        view(fragment, R.id.btnSignOut).performClick()

        assertEquals(R.id.startPageFragment, navController.currentDestination?.id)
        assertEquals(View.GONE, view(fragment, R.id.btnSignOut).visibility)
        assertEquals(appContext.getString(R.string.profile_guest), textOf(fragment, R.id.userName))
    }

    @Test
    fun `back click navigates up to start page`() {
        val fragment = launchFragment(currentUser = null)
        val navController = TestNavHostController(appContext).apply {
            setGraph(R.navigation.nav_graph)
            setCurrentDestination(R.id.startPageFragment)
            navigate(R.id.action_start_to_profile)
        }
        Navigation.setViewNavController(fragment.requireView(), navController)

        view(fragment, R.id.onBack).performClick()

        assertEquals(R.id.startPageFragment, navController.currentDestination?.id)
    }

    private fun launchFragment(
        currentUser: FirebaseUser?,
        totalScore: Int = 0,
        top5Answered: Int = 0,
        uefaAnswered: Int = 0,
        worldAnswered: Int = 0,
        versusAnswered: Int = 0,
        currentUserProviderOverride: (() -> FirebaseUser?)? = null,
        signOutActionOverride: (((() -> Unit) -> Unit))? = null
    ): ProfileFragment {
        val controller = Robolectric.buildActivity(TestHostActivity::class.java).setup()
        val activity = controller.get()
        val fragment = ProfileFragment().apply {
            this.currentUserProvider = currentUserProviderOverride ?: { currentUser }
            this.signOutAction = signOutActionOverride ?: { onComplete -> onComplete() }
            this.totalScoreProvider = { totalScore }
            this.top5AnsweredProvider = { top5Answered }
            this.uefaAnsweredProvider = { uefaAnswered }
            this.worldAnsweredProvider = { worldAnswered }
            this.versusAnsweredProvider = { versusAnswered }
            this.profileImageRenderer = {}
            this.localImageRenderer = {}
            this.defaultImageRenderer = {}
        }

        activity.supportFragmentManager.beginTransaction()
            .replace(TestHostActivity.CONTAINER_ID, fragment)
            .commitNow()

        return fragment
    }

    private fun mockUser(uid: String, name: String, email: String): FirebaseUser {
        val user = Mockito.mock(FirebaseUser::class.java)
        Mockito.`when`(user.uid).thenReturn(uid)
        Mockito.`when`(user.displayName).thenReturn(name)
        Mockito.`when`(user.email).thenReturn(email)
        Mockito.`when`(user.photoUrl).thenReturn(null as Uri?)
        return user
    }

    private fun view(fragment: ProfileFragment, id: Int): View =
        fragment.requireView().findViewById(id)

    private fun textOf(fragment: ProfileFragment, id: Int): String =
        fragment.requireView().findViewById<TextView>(id).text.toString()

    class TestHostActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            setTheme(R.style.FootballQuiz)
            super.onCreate(savedInstanceState)
            setContentView(FrameLayout(this).apply { id = CONTAINER_ID })
        }

        companion object {
            const val CONTAINER_ID = 1003
        }
    }

    class TestApplication : Application() {
        override fun onCreate() {
            super.onCreate()
            FirebaseApp.initializeApp(this)
        }
    }
}
