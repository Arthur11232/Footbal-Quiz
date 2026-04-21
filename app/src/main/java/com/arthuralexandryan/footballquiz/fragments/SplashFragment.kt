package com.arthuralexandryan.footballquiz.fragments

import android.animation.Animator
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arthuralexandryan.footballquiz.animations.BallFlightAnimationManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.FQ_Application
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.constants.Constant.INIT_DB
import com.arthuralexandryan.footballquiz.databinding.SplashBinding
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.utils.GameSoundManager
import com.arthuralexandryan.footballquiz.utils.Prefer
import java.util.Random

class SplashFragment : Fragment() {

    private var _binding: SplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DB_Helper
    private var splashAnimator: AnimatorSet? = null
    private val splashImageWidth = 320f
    private val splashImageHeight = 480f
    private val goalBoundsOnSource = RectF(88f, 124f, 233f, 173f)
    private var soundManager: GameSoundManager? = null
    private var pendingSoundRunnable: Runnable? = null
    private var isSplashAnimationFinished: Boolean = false
    private var isSplashSoundFinished: Boolean = true
    private val random = Random()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        soundManager = GameSoundManager(requireContext())
        initView()
    }

    override fun onStart() {
        super.onStart()
        binding.root.post {
            if (!isAdded || _binding == null) return@post
            startSplashAnimation()
        }
    }

    private fun initDBs() {
        if (Prefer.getBooleanPreference(requireContext(), INIT_DB, true)) {
            Prefer.setBooleanPreference(requireContext(), INIT_DB, false)
            Log.e("DB", "Initialization DB")
            FQ_Application.getInstance().setDB(dbHelper, true)
        }
    }

    private fun startSplashAnimation() {
        isSplashAnimationFinished = false
        isSplashSoundFinished = false

        val goalBounds = mapGoalBoundsToScreen() ?: run {
            if (isAdded) {
                findNavController().navigate(R.id.action_splash_to_start)
            }
            return
        }

        val flightSpec = BallFlightAnimationManager.FlightSpec(
            startCenterX = binding.loadingBall.x + binding.loadingBall.width / 2f,
            startCenterY = binding.loadingBall.y + binding.loadingBall.height / 2f,
            targetRect = goalBounds,
            durationMinMs = 5000L,
            durationMaxMs = 5000L,
            finalScaleMin = 0.12f,
            finalScaleMax = 0.2f,
            turnsMin = 4.5f,
            turnsMax = 7.5f,
            controlPointMin = 0.4f,
            controlPointMax = 0.7f,
            controlXOffsetMin = 0f,
            controlXOffsetMax = 0f,
            arcHeightBaseMin = binding.root.height * 0.06f,
            arcHeightDistanceFactor = 0f,
            arcHeightExtraMin = 0f,
            arcHeightExtraMax = binding.root.height * 0.10f
        )

        val flightPlan = BallFlightAnimationManager.buildPlan(flightSpec, random)

        splashAnimator?.cancel()
        splashAnimator = BallFlightAnimationManager.createAnimator(
            binding.loadingBall,
            flightPlan,
            flightSpec.interpolator
        ).apply {
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    initDBs()
                    scheduleGoalSound(startDelayMs = 2700L)
                }

                override fun onAnimationEnd(animation: Animator) {
                    clearPendingSound()
                    isSplashAnimationFinished = true
                    navigateFromSplashIfReady()
                }

                override fun onAnimationCancel(animation: Animator) {
                    clearPendingSound()
                    isSplashAnimationFinished = true
                    isSplashSoundFinished = true
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })
            start()
        }
    }

    private fun scheduleGoalSound(startDelayMs: Long) {
        clearPendingSound()
        val rootView = _binding?.root ?: return
        val runnable = Runnable {
            if (_binding == null || !isAdded) return@Runnable
            soundManager?.playRightAnswer {
                activity?.runOnUiThread {
                    isSplashSoundFinished = true
                    navigateFromSplashIfReady()
                }
            }
        }
        pendingSoundRunnable = runnable
        rootView.postDelayed(runnable, startDelayMs)
    }

    private fun clearPendingSound() {
        val rootView = _binding?.root
        val runnable = pendingSoundRunnable
        if (rootView != null && runnable != null) {
            rootView.removeCallbacks(runnable)
        }
        pendingSoundRunnable = null
    }

    private fun navigateFromSplashIfReady() {
        if (!isAdded || _binding == null) return
        if (!isSplashAnimationFinished || !isSplashSoundFinished) return
        if (findNavController().currentDestination?.id == R.id.splashFragment) {
            findNavController().navigate(R.id.action_splash_to_start)
        }
    }

    private fun mapGoalBoundsToScreen(): RectF? {
        val drawable = binding.fone.drawable ?: return null
        val matrixValues = FloatArray(9)
        binding.fone.imageMatrix.getValues(matrixValues)

        val scaleX = matrixValues[Matrix.MSCALE_X]
        val scaleY = matrixValues[Matrix.MSCALE_Y]
        val translateX = matrixValues[Matrix.MTRANS_X]
        val translateY = matrixValues[Matrix.MTRANS_Y]

        val displayedBounds = RectF(
            translateX,
            translateY,
            translateX + drawable.intrinsicWidth * scaleX,
            translateY + drawable.intrinsicHeight * scaleY
        )

        val widthRatio = displayedBounds.width() / splashImageWidth
        val heightRatio = displayedBounds.height() / splashImageHeight

        return RectF(
            displayedBounds.left + goalBoundsOnSource.left * widthRatio,
            displayedBounds.top + goalBoundsOnSource.top * heightRatio,
            displayedBounds.left + goalBoundsOnSource.right * widthRatio,
            displayedBounds.top + goalBoundsOnSource.bottom * heightRatio
        )
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val versionName = try {
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            pInfo.versionName
        } catch (e: Exception) {
            "1.0"
        }
        binding.textVersion.text = "${getString(R.string.version)} $versionName"
        dbHelper = DB_Helper()
    }

    override fun onDestroyView() {
        splashAnimator?.cancel()
        splashAnimator = null
        clearPendingSound()
        isSplashAnimationFinished = true
        isSplashSoundFinished = true
        soundManager?.release()
        soundManager = null
        super.onDestroyView()
        _binding = null
    }
}
