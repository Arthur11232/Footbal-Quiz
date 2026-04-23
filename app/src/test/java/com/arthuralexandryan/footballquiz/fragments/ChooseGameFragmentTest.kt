package com.arthuralexandryan.footballquiz.fragments

import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.arthuralexandryan.footballquiz.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = ChooseGameFragmentTest.TestApplication::class)
class ChooseGameFragmentTest {

    private val appContext = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun `score hints are visible when uefa and world are still locked`() {
        val fragment = launchFragment(top5Answered = 120, uefaAnswered = 40)

        val uefaHint = fragment.requireView().findViewById<TextView>(R.id.txt_score_uefa)
        val worldHint = fragment.requireView().findViewById<TextView>(R.id.txt_score_world)

        assertEquals(View.VISIBLE, uefaHint.visibility)
        assertEquals("120 / 150", uefaHint.text.toString())
        assertEquals(View.VISIBLE, worldHint.visibility)
        assertEquals("160 / 210", worldHint.text.toString())
    }

    @Test
    fun `score hints are hidden when thresholds are already reached`() {
        val fragment = launchFragment(top5Answered = 150, uefaAnswered = 60)

        val uefaHint = fragment.requireView().findViewById<TextView>(R.id.txt_score_uefa)
        val worldHint = fragment.requireView().findViewById<TextView>(R.id.txt_score_world)

        assertEquals(View.GONE, uefaHint.visibility)
        assertEquals(View.GONE, worldHint.visibility)
    }

    @Test
    fun `top tournament always navigates to category screen`() {
        val fragment = launchFragment(top5Answered = 0, uefaAnswered = 0)
        val navController = attachNavController(fragment)

        fragment.requireView().findViewById<View>(R.id.btnTop).performClick()

        assertEquals(R.id.categoryPageFragment, navController.currentDestination?.id)
        assertEquals("top5", navController.backStack.last().arguments?.getString("gameScore"))
    }

    @Test
    fun `uefa click shows toast when points are not enough`() {
        val fragment = launchFragment(top5Answered = 149, uefaAnswered = 0)
        val navController = attachNavController(fragment)

        fragment.requireView().findViewById<View>(R.id.btnUefa).performClick()

        assertEquals(appContext.getString(R.string.points_not_enough), ShadowToast.getTextOfLatestToast())
        assertEquals(R.id.chooseGameFragment, navController.currentDestination?.id)
    }

    @Test
    fun `uefa click navigates when enough top5 points exist`() {
        val fragment = launchFragment(top5Answered = 150, uefaAnswered = 0)
        val navController = attachNavController(fragment)

        fragment.requireView().findViewById<View>(R.id.btnUefa).performClick()

        assertEquals(R.id.categoryPageFragment, navController.currentDestination?.id)
        assertEquals("uefa", navController.backStack.last().arguments?.getString("gameScore"))
    }

    @Test
    fun `world click shows toast when combined points are not enough`() {
        val fragment = launchFragment(top5Answered = 150, uefaAnswered = 59)
        val navController = attachNavController(fragment)

        fragment.requireView().findViewById<View>(R.id.btnChamp).performClick()

        assertEquals(appContext.getString(R.string.points_not_enough), ShadowToast.getTextOfLatestToast())
        assertEquals(R.id.chooseGameFragment, navController.currentDestination?.id)
    }

    @Test
    fun `world click navigates when combined points reach threshold`() {
        val fragment = launchFragment(top5Answered = 150, uefaAnswered = 60)
        val navController = attachNavController(fragment)

        fragment.requireView().findViewById<View>(R.id.btnChamp).performClick()

        assertEquals(R.id.categoryPageFragment, navController.currentDestination?.id)
        assertEquals("world", navController.backStack.last().arguments?.getString("gameScore"))
    }

    @Test
    fun `versus click navigates to versus screen`() {
        val fragment = launchFragment(top5Answered = 0, uefaAnswered = 0)
        val navController = attachNavController(fragment)

        fragment.requireView().findViewById<View>(R.id.btnVersus).performClick()

        assertEquals(R.id.versusFragment, navController.currentDestination?.id)
    }

    @Test
    fun `back click navigates up to start page`() {
        val fragment = launchFragment(top5Answered = 0, uefaAnswered = 0)
        val navController = TestNavHostController(appContext).apply {
            setGraph(R.navigation.nav_graph)
            setCurrentDestination(R.id.startPageFragment)
            Navigation.setViewNavController(fragment.requireView(), this)
            navigate(R.id.action_start_to_choose)
        }

        fragment.requireView().findViewById<View>(R.id.onBack).performClick()

        assertEquals(R.id.startPageFragment, navController.currentDestination?.id)
    }

    private fun launchFragment(top5Answered: Int, uefaAnswered: Int): ChooseGameFragment {
        val controller = Robolectric.buildActivity(TestHostActivity::class.java).setup()
        val activity = controller.get()
        val fragment = ChooseGameFragment().apply {
            top5AnsweredProvider = { top5Answered }
            uefaAnsweredProvider = { uefaAnswered }
        }

        activity.supportFragmentManager.beginTransaction()
            .replace(TestHostActivity.CONTAINER_ID, fragment)
            .commitNow()

        return fragment
    }

    private fun attachNavController(
        fragment: ChooseGameFragment,
        currentDestination: Int = R.id.chooseGameFragment
    ): TestNavHostController {
        return TestNavHostController(appContext).apply {
            setGraph(R.navigation.nav_graph)
            setCurrentDestination(currentDestination)
            Navigation.setViewNavController(fragment.requireView(), this)
        }
    }

    class TestHostActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            setTheme(R.style.FootballQuiz)
            super.onCreate(savedInstanceState)
            setContentView(FrameLayout(this).apply { id = CONTAINER_ID })
        }

        companion object {
            const val CONTAINER_ID = 1002
        }
    }

    class TestApplication : Application()
}
