package com.arthuralexandryan.footballquiz.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.RectF
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.animations.BallFlightAnimationManager
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.databinding.ActivityPlayNewBinding
import com.arthuralexandryan.footballquiz.interfaces.ShowAds
import com.arthuralexandryan.footballquiz.models.AdBanner
import com.arthuralexandryan.footballquiz.models.AdMobPresenter
import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.presentation.play.PlayAnswerOption
import com.arthuralexandryan.footballquiz.presentation.play.PlayEffect
import com.arthuralexandryan.footballquiz.presentation.play.PlaySessionArgs
import com.arthuralexandryan.footballquiz.presentation.play.PlayUiState
import com.arthuralexandryan.footballquiz.presentation.play.PlayViewModel
import com.arthuralexandryan.footballquiz.utils.SystemBarStyleHelper
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Random

class PlayFragment : Fragment(), ShowAds {

    private var _binding: ActivityPlayNewBinding? = null
    private val binding get() = _binding!!

    private val playViewModel: PlayViewModel by viewModel()
    private var categoryKey: String? = null
    private var answerDelay: Long = 2000

    private var adMob: AdBanner? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedAd: RewardedAd? = null
    private lateinit var adMobPresenter: AdMobPresenter
    private var goalAnimator: AnimatorSet? = null
    private var flyingGoalBall: ImageView? = null
    private var answerTimer: CountDownTimer? = null
    private val random = Random()
    private var pendingAnswerResult: Boolean? = null
    private var selectedAnswerView: View? = null
    private var pendingRewardResetForce: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActivityPlayNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.FQBannerAdView.visibility = View.GONE
        if (Constant.ADS_ENABLED) {
            binding.FQBannerAdView.visibility = View.VISIBLE
            adMobPresenter = AdMobPresenter(this)
            adMob = AdBanner(requireActivity(), binding.FQBannerAdView as ViewGroup, getString(R.string.banner_ad_unit_id))
            loadInterstitialAd()
            loadRewardedAd()
        }

        observeUiState()
        observeEffects()
        initUiActions()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackNavigation()
        }

        arguments?.let {
            processArguments(it)
        }
    }

    override fun onResume() {
        super.onResume()
        SystemBarStyleHelper.applySampledDrawable(
            fragment = this,
            drawableResId = R.drawable.question_bg,
            fallbackColor = ContextCompat.getColor(requireContext(), R.color.fq_colorPrimaryDark),
            matchNavigationToStatus = true,
            lightSystemBarIcons = false
        )
    }

    private fun loadInterstitialAd() {
        if (!Constant.ADS_ENABLED) return

        val context = context ?: return
        if (!isAdded || _binding == null) return

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, getString(R.string.interstitial_ad_unit_id), adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    if (_binding == null) return
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            mInterstitialAd = null
                            if (_binding == null || !isAdded) return
                            loadInterstitialAd()
                        }
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }
            })
    }

    private fun loadRewardedAd() {
        if (!Constant.ADS_ENABLED) return

        val context = context ?: return
        if (!isAdded || _binding == null) return

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, getString(R.string.video_ad_unit_id), adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    if (_binding == null) return
                    mRewardedAd = rewardedAd
                    mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            mRewardedAd = null
                            if (_binding == null || !isAdded) return
                            loadRewardedAd()
                        }
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mRewardedAd = null
                }
            })
    }

    private fun initUiActions() {
        binding.forceReset.setOnClickListener {
            if (!Constant.ADS_ENABLED) {
                playViewModel.resetSession(isForce = true)
            } else if (mRewardedAd != null) {
                pendingRewardResetForce = true
                mRewardedAd?.show(requireActivity()) { rewardItem ->
                    adMobPresenter.onShow(rewardItem.amount)
                }
            } else {
                playViewModel.resetSession(isForce = true)
                loadRewardedAd()
            }
        }

        binding.forceExit.setOnClickListener {
            handleBackNavigation()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                playViewModel.uiState.collect { state ->
                    if (_binding == null) return@collect
                    render(state)
                }
            }
        }
    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                playViewModel.effects.collect { effect ->
                    when (effect) {
                        is PlayEffect.AnimateAnswer -> {
                            pendingAnswerResult = effect.isCorrect
                            binding.root.post {
                                if (_binding == null || !isAdded) return@post
                                shootBallToGoal(isGoal = effect.isCorrect) {}
                            }
                            runAnswerTransition()
                        }
                        is PlayEffect.ShowAchievementCertificate -> {
                            navigateToAchievementCongratulations(effect.payload)
                        }
                        PlayEffect.ShowInterstitialAd -> {
                            if (Constant.ADS_ENABLED && mInterstitialAd != null) {
                                mInterstitialAd?.show(requireActivity())
                            } else if (Constant.ADS_ENABLED) {
                                loadInterstitialAd()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToAchievementCongratulations(
        payload: com.arthuralexandryan.footballquiz.presentation.achievement.AchievementCertificatePayload
    ) {
        playViewModel.onAchievementCertificateOpened()
        val args = Bundle().apply {
            putParcelable("certificatePayload", payload)
        }
        findNavController().navigate(
            R.id.action_play_to_achievement_congratulations,
            args
        )
    }

    private fun processArguments(data: Bundle) {
        val questionArray = data.getParcelableArray("harcer") as? Array<GameObjectSerializable>
        val questions = questionArray?.toList().orEmpty()
        val placeScores = data.getParcelable<GameObjectScores>("achokner") ?: GameObjectScores()
        val categoryScores = data.getParcelable<GameObjectScores>("cat_score") ?: GameObjectScores()
        val titleText = data.getString("title").orEmpty()
        categoryKey = data.getString("category")

        if (questions.isNotEmpty()) {
            playViewModel.initialize(
                PlaySessionArgs(
                    questions = questions,
                    placeScores = placeScores,
                    categoryScores = categoryScores,
                    titleText = titleText,
                    categoryKey = categoryKey.orEmpty()
                )
            )
        }
    }

    private fun render(state: PlayUiState) {
        binding.scoreboard.placeNameScores.text = state.titleText
        binding.scoreboard.placeQuestion.text = state.placeQuestionText
        binding.scoreboard.placeAnswer.text = state.placeAnswerText
        binding.scoreboard.categoryAnswers.text = state.categoryAnswersText
        binding.scoreboard.categoryScore.text = state.categoryScoreText
        binding.question.text = state.questionText
        binding.questionNumber.text = state.questionNumberText
        binding.forceReset.alpha = state.forceResetAlpha
        binding.forceReset.isEnabled = state.isResetEnabled

        bindAnswerView(binding.answerA.root, state.answerOptions.getOrNull(0))
        bindAnswerView(binding.answerB.root, state.answerOptions.getOrNull(1))
        bindAnswerView(binding.answerC.root, state.answerOptions.getOrNull(2))
        bindAnswerView(binding.answerD.root, state.answerOptions.getOrNull(3))

        if (state.answerFeedbackResId != null && state.isAnswerCorrect != null) {
            binding.answerIsCorrect.setText(state.answerFeedbackResId)
            binding.answerIsCorrect.setBackgroundResource(
                if (state.isAnswerCorrect) R.drawable.toast_shape_true else R.drawable.toast_shape_false
            )
            binding.answerIsCorrect.visibility = View.VISIBLE
        } else {
            binding.answerIsCorrect.visibility = View.GONE
        }

        if (state.showFinishScreen) {
            showFinishPage(state.totalGoalText)
        } else {
            binding.llHarcer.visibility = View.VISIBLE
            binding.finishGame.root.visibility = View.GONE
            binding.lNumber.visibility = View.VISIBLE
        }
    }

    private fun bindAnswerView(answerView: View, option: PlayAnswerOption?) {
        val answerText = answerView.findViewById<TextView>(R.id.answer)
        answerView.findViewById<TextView>(R.id.answer_number).text = option?.id.orEmpty()
        answerText.text = option?.text.orEmpty()
        answerView.isEnabled = option != null && playViewModel.uiState.value.isAnswerEnabled

        answerView.setOnClickListener {
            if (option != null && playViewModel.uiState.value.isAnswerEnabled) {
                selectedAnswerView?.findViewById<ImageView>(R.id.answer_image)?.setImageResource(R.color.fq_colorWhite)
                selectedAnswerView = answerView
                answerView.findViewById<ImageView>(R.id.answer_image).setImageResource(R.color.fq_colorWaitingAnswer)
                playViewModel.onAnswerSelected(option.text)
            }
        }
    }

    private fun runAnswerTransition() {
        answerTimer?.cancel()
        answerTimer = object : CountDownTimer(answerDelay, 1600) {
            override fun onTick(millisUntilFinished: Long) = Unit

            override fun onFinish() {
                answerTimer = null
                if (_binding == null || !isAdded) return

                selectedAnswerView?.findViewById<ImageView>(R.id.answer_image)?.setImageResource(R.color.fq_colorWhite)
                selectedAnswerView = null
                pendingAnswerResult = null
                playViewModel.onAnswerTransitionFinished()
            }
        }.start()
    }

    /**
     * Uses a temporary overlay ball so the scoreboard's small nested layouts
     * cannot clip the goal animation.
     */
    private fun shootBallToGoal(isGoal: Boolean = true, onGoal: () -> Unit) {
        val currentBinding = _binding ?: return
        val root = currentBinding.root as ViewGroup
        val sourceBall = currentBinding.scoreboard.questionBall
        val goal = currentBinding.scoreboard.answerBox

        cancelGoalAnimation()

        sourceBall.alpha = 0.25f
        goal.scaleX = 1f
        goal.scaleY = 1f

        val rootLoc = IntArray(2)
        val ballLoc = IntArray(2)
        val goalLoc = IntArray(2)
        root.getLocationOnScreen(rootLoc)
        sourceBall.getLocationOnScreen(ballLoc)
        goal.getLocationOnScreen(goalLoc)

        val ballCenterX = ballLoc[0] + sourceBall.width / 2f
        val ballCenterY = ballLoc[1] + sourceBall.height / 2f
        val goalCenterX = goalLoc[0] + goal.width / 2f
        val goalCenterY = goalLoc[1] + goal.height / 2f

        val density = resources.displayMetrics.density
        val ballSize = maxOf(sourceBall.width, sourceBall.height, (12f * density).toInt())
        val startX = ballCenterX - rootLoc[0] - ballSize / 2f
        val startY = ballCenterY - rootLoc[1] - ballSize / 2f
        val goalLeft = goalLoc[0] - rootLoc[0].toFloat()
        val goalTop = goalLoc[1] - rootLoc[1].toFloat()
        val goalRight = goalLeft + goal.width
        val goalBottom = goalTop + goal.height
        val goalBallCenterX = goalCenterX - rootLoc[0]
        val goalBallCenterY = goalCenterY - rootLoc[1]

        val targetRect = if (isGoal) {
            BallFlightAnimationManager.createTargetRect(
                centerX = goalBallCenterX,
                centerY = goalBallCenterY,
                spreadX = 0f,
                spreadY = 0f
            )
        } else {
            when (random.nextInt(3)) {
                0 -> createMissTargetRect(
                    centerX = goalLeft - ballSize * 0.32f - randomRange(4f * density, 12f * density),
                    centerY = goalTop + goal.height * 0.52f,
                    spreadX = 4f * density,
                    spreadY = goal.height * 0.08f
                )

                1 -> createMissTargetRect(
                    centerX = goalRight + ballSize * 0.32f + randomRange(4f * density, 12f * density),
                    centerY = goalTop + goal.height * 0.52f,
                    spreadX = 4f * density,
                    spreadY = goal.height * 0.08f
                )

                else -> Pair(
                    goalBallCenterX + randomRange(-goal.width * 0.12f, goal.width * 0.12f),
                    goalTop - ballSize * 0.45f - randomRange(6f * density, 18f * density)
                )
                    .let { (centerX, centerY) ->
                        createMissTargetRect(
                            centerX = centerX,
                            centerY = centerY,
                            spreadX = goal.width * 0.1f,
                            spreadY = 6f * density
                        )
                    }
            }
        }

        val flyingBall = ImageView(requireContext()).apply {
            setImageResource(R.drawable.ball)
            scaleType = ImageView.ScaleType.FIT_CENTER
            x = startX
            y = startY
            alpha = 0f
            scaleX = 1.05f
            scaleY = 1.05f
            elevation = 12f * density
        }
        root.addView(flyingBall, ViewGroup.LayoutParams(ballSize, ballSize))
        flyingGoalBall = flyingBall

        val flightSpec = if (isGoal) {
            BallFlightAnimationManager.FlightSpec(
                startCenterX = ballCenterX - rootLoc[0],
                startCenterY = ballCenterY - rootLoc[1],
                targetRect = targetRect,
                durationMinMs = 760L,
                durationMaxMs = 760L,
                finalScaleMin = 0.9f,
                finalScaleMax = 0.9f,
                turnsMin = 1.5f,
                turnsMax = 1.5f,
                controlPointMin = 0.5f,
                controlPointMax = 0.5f,
                controlXOffsetMin = 0f,
                controlXOffsetMax = 0f,
                arcHeightBaseMin = 18f * density,
                arcHeightDistanceFactor = 0.12f,
                arcHeightExtraMin = 0f,
                arcHeightExtraMax = 0f,
                interpolator = DecelerateInterpolator()
            )
        } else {
            BallFlightAnimationManager.FlightSpec(
                startCenterX = ballCenterX - rootLoc[0],
                startCenterY = ballCenterY - rootLoc[1],
                targetRect = targetRect,
                durationMinMs = 700L,
                durationMaxMs = 840L,
                finalScaleMin = 0.9f,
                finalScaleMax = 0.95f,
                turnsMin = 1.2f,
                turnsMax = 1.45f,
                controlPointMin = 0.5f,
                controlPointMax = 0.5f,
                controlXOffsetMin = -10f * density,
                controlXOffsetMax = 10f * density,
                arcHeightBaseMin = 20f * density,
                arcHeightDistanceFactor = 0.11f,
                arcHeightExtraMin = 6f * density,
                arcHeightExtraMax = 18f * density,
                interpolator = DecelerateInterpolator()
            )
        }

        val goalPulse = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(goal, "scaleX", 1f, 1.2f, 1f),
                ObjectAnimator.ofFloat(goal, "scaleY", 1f, 1.2f, 1f)
            )
            duration = 260
            interpolator = OvershootInterpolator()
        }

        val flightPlan = BallFlightAnimationManager.buildPlan(flightSpec, random)
        root.post {
            if (_binding == null || !isAdded || flyingGoalBall !== flyingBall) return@post

            flyingBall.x = startX
            flyingBall.y = startY
            flyingBall.alpha = 1f

            val phase1 = BallFlightAnimationManager.createAnimator(flyingBall, flightPlan, flightSpec.interpolator)
            var phaseOneCancelled = false
            phase1.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    phaseOneCancelled = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (phaseOneCancelled || _binding == null) return
                    onGoal()
                    if (isGoal) {
                        goalPulse.start()
                    }
                }
            })

            goalAnimator = AnimatorSet().apply {
                playSequentially(phase1)
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        sourceBall.alpha = 1f
                        goal.scaleX = 1f
                        goal.scaleY = 1f
                        (flyingBall.parent as? ViewGroup)?.removeView(flyingBall)
                        if (flyingGoalBall === flyingBall) {
                            flyingGoalBall = null
                        }
                        if (goalAnimator === animation) {
                            goalAnimator = null
                        }
                    }
                })
                start()
            }
        }
    }

    private fun cancelGoalAnimation() {
        goalAnimator?.cancel()
        goalAnimator = null
        flyingGoalBall?.let { flyingBall ->
            (flyingBall.parent as? ViewGroup)?.removeView(flyingBall)
        }
        flyingGoalBall = null
        _binding?.scoreboard?.questionBall?.alpha = 1f
        _binding?.scoreboard?.answerBox?.scaleX = 1f
        _binding?.scoreboard?.answerBox?.scaleY = 1f
    }

    private fun createMissTargetRect(centerX: Float, centerY: Float, spreadX: Float, spreadY: Float): RectF {
        return BallFlightAnimationManager.createTargetRect(centerX, centerY, spreadX, spreadY)
    }

    private fun randomRange(min: Float, max: Float): Float {
        return min + random.nextFloat() * (max - min)
    }

    private fun showFinishPage(totalScoreText: String) {
        binding.llHarcer.visibility = View.GONE
        binding.finishGame.root.visibility = View.VISIBLE
        binding.lNumber.visibility = View.INVISIBLE
        
        binding.finishGame.totalGoal.text = totalScoreText
        
        binding.finishGame.exitGame.setOnClickListener{
            handleBackNavigation()
        }
        
        binding.finishGame.reloadGame.setOnClickListener {
            if (!Constant.ADS_ENABLED) {
                playViewModel.resetSession(isForce = false)
            } else if (mRewardedAd != null) {
                pendingRewardResetForce = false
                mRewardedAd?.show(requireActivity()) { rewardItem ->
                    adMobPresenter.onShow(rewardItem.amount)
                }
            } else {
                playViewModel.resetSession(isForce = false)
                loadRewardedAd()
            }
        }
    }

    private fun handleBackNavigation() {
        val navController = findNavController()
        if (!navController.navigateUp()) {
            navController.popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        playViewModel.onHostStopped()
        playViewModel.persistCurrentQuestionOnStop()
        playViewModel.syncProgressIfSignedIn()
        super.onStop()
    }

    override fun onDestroyView() {
        answerTimer?.cancel()
        answerTimer = null
        cancelGoalAnimation()
        adMob?.destroyLoading()
        mInterstitialAd = null
        mRewardedAd = null
        _binding = null
        super.onDestroyView()
    }

    override fun show(amount: Int) {
        if (_binding == null || !isAdded) return
        playViewModel.resetSession(isForce = pendingRewardResetForce)
    }
}
