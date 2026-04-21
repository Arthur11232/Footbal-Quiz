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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.animations.BallFlightAnimationManager
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.databinding.ActivityPlayNewBinding
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.interfaces.ResetGame
import com.arthuralexandryan.footballquiz.interfaces.ShowAds
import com.arthuralexandryan.footballquiz.models.AdBanner
import com.arthuralexandryan.footballquiz.models.AdMobPresenter
import com.arthuralexandryan.footballquiz.models.CloudSyncManager
import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.models.ScoreboardModel
import com.arthuralexandryan.footballquiz.utils.GameSoundManager
import com.arthuralexandryan.footballquiz.utils.Prefer
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import java.util.Random

class PlayFragment : Fragment(), ResetGame, ShowAds {

    private var _binding: ActivityPlayNewBinding? = null
    private val binding get() = _binding!!

    private lateinit var questions: List<GameObjectSerializable>
    private var flagForAnswer: Boolean = false
    private lateinit var placeScores: GameObjectScores
    private lateinit var categoryScores: GameObjectScores
    private lateinit var dbHelper: DB_Helper
    private var titleText: String? = null
    private var categoryType: String? = null

    private var answers: MutableList<String> = mutableListOf()
    private var nextQuestion: Int = 1
    private var answerDelay: Long = 2000
    private var isResetEnable: Boolean = false
    private var n: Int = 0

    private var adMob: AdBanner? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedAd: RewardedAd? = null
    private var counter: Int = 0
    private var forceReloadBarrier: Int = 20
    private lateinit var adMobPresenter: AdMobPresenter
    private var goalAnimator: AnimatorSet? = null
    private var flyingGoalBall: ImageView? = null
    private var answerTimer: CountDownTimer? = null
    private var soundManager: GameSoundManager? = null
    private val random = Random()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActivityPlayNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        soundManager = GameSoundManager(requireContext())

        binding.FQBannerAdView.visibility = View.GONE
        if (Constant.ADS_ENABLED) {
            binding.FQBannerAdView.visibility = View.VISIBLE
            adMobPresenter = AdMobPresenter(this)
            adMob = AdBanner(requireActivity(), binding.FQBannerAdView as ViewGroup, getString(R.string.banner_ad_unit_id))
            loadInterstitialAd()
            loadRewardedAd()
        }

        arguments?.let {
            processArguments(it)
        }
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

    private fun processArguments(data: Bundle) {
        val harcerArr = data.getParcelableArray("harcer") as? Array<GameObjectSerializable>
        questions = harcerArr?.toList() ?: emptyList()
        placeScores = data.getParcelable<GameObjectScores>("achokner") ?: GameObjectScores()
        categoryScores = data.getParcelable<GameObjectScores>("cat_score") ?: GameObjectScores()
        titleText = data.getString("title")
        categoryType = data.getString("category")

        if (questions.isNotEmpty()) {
            dbHelper = DB_Helper(requireActivity(), questions[0].categoryType, questions[0].type)
            initUI()
            setViewsValues()
        }
    }

    private fun initUI() {
        binding.forceReset.alpha = 0.2f
        binding.forceReset.isEnabled = isResetEnable
        
        binding.forceReset.setOnClickListener {
            if (!Constant.ADS_ENABLED) {
                dbHelper.resetPlace(placeScores.place_score_answer, this, true)
                isResetEnable = false
            } else if (mRewardedAd != null) {
                mRewardedAd?.show(requireActivity()) { rewardItem ->
                    adMobPresenter.onShow(rewardItem.amount)
                }
            } else {
                dbHelper.resetPlace(placeScores.place_score_answer, this, true)
                isResetEnable = false
                loadRewardedAd()
            }
        }
        
        binding.forceExit.setOnClickListener {
            handleBackNavigation()
        }
    }

    private fun setViewsValues() {
        binding.scoreboard.placeNameScores.text = titleText
        binding.scoreboard.placeQuestion.text = placeScores.placeScoreText
        binding.scoreboard.placeAnswer.text = placeScores.placeScoreAnswerText
        binding.scoreboard.categoryAnswers.text = categoryScores.placeScoreAnswerText
        binding.scoreboard.categoryScore.text = categoryScores.category_score
        
        nextQuestion = placeScores.place_score
        forceReloadBarrier = placeScores.place_score / 2
        newGame()
    }

    private fun newGame() {
        binding.answerIsCorrect.visibility = View.GONE
        flagForAnswer = true

        if (placeScores.place_score <= 0) {
            showFinishPage()
            return
        }

        val unansweredIndexes = questions.indices.filter { !questions[it].isAnswered }
        if (unansweredIndexes.isEmpty()) {
            placeScores.place_score = 0
            binding.scoreboard.placeQuestion.text = placeScores.placeScoreText
            updateDBScores()
            showFinishPage()
            return
        }

        if (placeScores.place_score > unansweredIndexes.size) {
            placeScores.place_score = unansweredIndexes.size
            binding.scoreboard.placeQuestion.text = placeScores.placeScoreText
            updateDBScores()
        }

        answers = mutableListOf()
        val savedStateKey = getSavedQuestionStateKey()
        val savedState = Prefer.getIntPreference(requireContext(), savedStateKey, -1)
        Prefer.setIntPreference(requireContext(), savedStateKey, -1)

        n = if (savedState in questions.indices && !questions[savedState].isAnswered) {
            savedState
        } else {
            unansweredIndexes[random.nextInt(unansweredIndexes.size)]
        }

        setAnswers()
    }

    @SuppressLint("SetTextI18n")
    private fun setAnswers() {
        answers.clear()
        questions[n].answer_A?.let { answers.add(it) }
        questions[n].answer_B?.let { answers.add(it) }
        questions[n].answer_C?.let { answers.add(it) }
        questions[n].answer_D?.let { answers.add(it) }
        
        binding.question.text = questions[n].question
        binding.questionNumber.text = "" + nextQuestion--
        
        setupAnswerView(binding.answerA.root, "1")
        setupAnswerView(binding.answerB.root, "2")
        setupAnswerView(binding.answerC.root, "3")
        setupAnswerView(binding.answerD.root, "4")
    }

    private fun setupAnswerView(answerView: View, number: String) {
        val answerText = answerView.findViewById<TextView>(R.id.answer)
        answerView.findViewById<TextView>(R.id.answer_number).text = number
        
        if (answers.isNotEmpty()) {
            val x = random.nextInt(answers.size)
            answerText.text = answers[x]
            answers.removeAt(x)
        }

        answerView.setOnClickListener {
            if (flagForAnswer) {
                flagForAnswer = false
                answerView.findViewById<ImageView>(R.id.answer_image).setImageResource(R.color.fq_colorWaitingAnswer)
                setTimerForChecking(answerView, answerText)
            }
        }
    }

    private fun setTimerForChecking(answerView: View, answer: TextView) {
        answerTimer?.cancel()
        answerTimer = object : CountDownTimer(answerDelay, 1600) {
            override fun onTick(millisUntilFinished: Long) {
                if (_binding == null) return
                if (millisUntilFinished >= 1600) {
                    checkingAnswer(answer)
                    counter++
                }
            }

            override fun onFinish() {
                answerTimer = null
                if (_binding == null || !isAdded) return

                answerView.findViewById<ImageView>(R.id.answer_image).setImageResource(R.color.fq_colorWhite)
                updateDBScores()
                newGame()
                if (forceReloadBarrier > placeScores.place_score) {
                    isResetEnable = true
                    binding.forceReset.isEnabled = true
                    binding.forceReset.alpha = 1f
                }
                if (counter == 5) {
                    counter = 0
                    if (Constant.ADS_ENABLED && mInterstitialAd != null) {
                        mInterstitialAd?.show(requireActivity())
                    } else if (Constant.ADS_ENABLED) {
                        loadInterstitialAd()
                    }
                }
            }
        }.start()
    }

    private fun checkingAnswer(answer: TextView) {
        if (check(answer.text.toString())) {
            binding.answerIsCorrect.setText(R.string.answer_correct)
            binding.answerIsCorrect.setBackgroundResource(R.drawable.toast_shape_true)
            binding.answerIsCorrect.visibility = View.VISIBLE
            categoryScores.place_score_answer = categoryScores.place_score_answer + 1
            placeScores.place_score_answer = placeScores.place_score_answer + 1
            dbHelper.setCategoryScores()
            setAnswered()
            placeScores.place_score = placeScores.place_score - 1
            shootBallToGoal {
                if (_binding == null) return@shootBallToGoal
                binding.scoreboard.categoryAnswers.text = categoryScores.placeScoreAnswerText
                binding.scoreboard.placeAnswer.text = placeScores.placeScoreAnswerText
                binding.scoreboard.placeQuestion.text = placeScores.placeScoreText
            }
        } else {
            binding.answerIsCorrect.setText(R.string.answer_incorect)
            binding.answerIsCorrect.setBackgroundResource(R.drawable.toast_shape_false)
            binding.answerIsCorrect.visibility = View.VISIBLE
            setAnswered()
            placeScores.place_score = placeScores.place_score - 1
            shootBallToGoal(isGoal = false) {
                if (_binding == null) return@shootBallToGoal
                binding.scoreboard.placeQuestion.text = placeScores.placeScoreText
            }
        }
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
                spreadX = goal.width * 0.22f,
                spreadY = goal.height * 0.16f
            )
        } else {
            when (random.nextInt(3)) {
                0 -> createMissTargetRect(
                    centerX = goalLeft - ballSize * 0.55f - randomRange(6f * density, 20f * density),
                    centerY = goalTop + goal.height * 0.45f,
                    spreadX = 6f * density,
                    spreadY = goal.height * 0.12f
                )

                1 -> createMissTargetRect(
                    centerX = goalRight + ballSize * 0.55f + randomRange(6f * density, 20f * density),
                    centerY = goalTop + goal.height * 0.45f,
                    spreadX = 6f * density,
                    spreadY = goal.height * 0.12f
                )

                else -> Pair(
                    goalBallCenterX,
                    goalTop - ballSize * 0.75f - randomRange(8f * density, 26f * density)
                )
                    .let { (centerX, centerY) ->
                        createMissTargetRect(
                            centerX = centerX,
                            centerY = centerY,
                            spreadX = goal.width * 0.18f,
                            spreadY = 8f * density
                        )
                    }
            }
        }

        val flyingBall = ImageView(requireContext()).apply {
            setImageResource(R.drawable.ball)
            scaleType = ImageView.ScaleType.FIT_CENTER
            x = startX
            y = startY
            scaleX = 1.05f
            scaleY = 1.05f
            elevation = 12f * density
        }
        root.addView(flyingBall, ViewGroup.LayoutParams(ballSize, ballSize))
        flyingGoalBall = flyingBall

        val flightSpec = BallFlightAnimationManager.FlightSpec(
            startCenterX = ballCenterX - rootLoc[0],
            startCenterY = ballCenterY - rootLoc[1],
            targetRect = targetRect,
            durationMinMs = 640L,
            durationMaxMs = 820L,
            finalScaleMin = 0.88f,
            finalScaleMax = 0.94f,
            turnsMin = 1.35f,
            turnsMax = 1.55f,
            controlPointMin = 0.5f,
            controlPointMax = 0.5f,
            controlXOffsetMin = -14f * density,
            controlXOffsetMax = 14f * density,
            arcHeightBaseMin = 22f * density,
            arcHeightDistanceFactor = 0.1f,
            arcHeightExtraMin = 10f * density,
            arcHeightExtraMax = 32f * density,
            interpolator = DecelerateInterpolator()
        )

        val goalPulse = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(goal, "scaleX", 1f, 1.2f, 1f),
                ObjectAnimator.ofFloat(goal, "scaleY", 1f, 1.2f, 1f)
            )
            duration = 260
            interpolator = OvershootInterpolator()
        }

        val flightPlan = BallFlightAnimationManager.buildPlan(flightSpec, random)
        val phase1 = BallFlightAnimationManager.createAnimator(flyingBall, flightPlan, flightSpec.interpolator)
        var phaseOneCancelled = false
        phase1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                if (isGoal) {
                    playGoalSound()
                } else {
                    playMissSound()
                }
            }

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

    private fun playGoalSound() {
        soundManager?.playRightAnswer()
    }

    private fun playMissSound() {
        soundManager?.playWrongAnswer()
    }

    private fun createMissTargetRect(centerX: Float, centerY: Float, spreadX: Float, spreadY: Float): RectF {
        return BallFlightAnimationManager.createTargetRect(centerX, centerY, spreadX, spreadY)
    }

    private fun randomOffset(radius: Float): Float {
        return randomRange(-radius, radius)
    }

    private fun randomRange(min: Float, max: Float): Float {
        return min + random.nextFloat() * (max - min)
    }

    private fun updateDBScores() {
        dbHelper.setPlaceScores(placeScores.place_score_answer, placeScores.place_score)
    }

    private fun setAnswered() {
        questions[n].isAnswered = true
        dbHelper.setAnswered(questions[n].question ?: "")
    }

    private fun check(answer: String): Boolean {
        return questions[n].right_answer.equals(answer, ignoreCase = true)
    }

    private fun showFinishPage() {
        binding.llHarcer.visibility = View.GONE
        binding.finishGame.root.visibility = View.VISIBLE
        binding.lNumber.visibility = View.INVISIBLE
        
        val totalGoal = binding.finishGame.root.findViewById<TextView>(R.id.total_goal)
        totalGoal.text = DB_Helper().getTotalScoreText()
        
        binding.finishGame.root.findViewById<ImageView>(R.id.exit_game).setOnClickListener {
            handleBackNavigation()
        }
        
        binding.finishGame.root.findViewById<ImageView>(R.id.reload_game).setOnClickListener {
            isResetEnable = false
            if (!Constant.ADS_ENABLED) {
                dbHelper.resetPlace(placeScores.place_score_answer, this, false)
            } else if (mRewardedAd != null) {
                mRewardedAd?.show(requireActivity()) { rewardItem ->
                    adMobPresenter.onShow(rewardItem.amount)
                }
            } else {
                dbHelper.resetPlace(placeScores.place_score_answer, this, false)
                loadRewardedAd()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun reset(placeScore: ScoreboardModel, isForce: Boolean) {
        questions.forEach { it.isAnswered = false }
        placeScores.place_score = placeScore.placeScore
        placeScores.place_score_answer = placeScore.placeAnswered
        categoryScores.place_score_answer = placeScore.categoryAnswered
        
        binding.scoreboard.placeQuestion.text = "" + placeScore.placeScore
        binding.scoreboard.placeAnswer.text = "" + placeScore.placeAnswered
        binding.scoreboard.categoryAnswers.text = "" + placeScore.categoryAnswered
        binding.scoreboard.categoryScore.text = "" + placeScore.categoryScore
        binding.questionNumber.text = "" + placeScore.placeScore
        
        if (!isForce) {
            binding.llHarcer.visibility = View.VISIBLE
            binding.finishGame.root.visibility = View.GONE
            binding.lNumber.visibility = View.VISIBLE
        }
        
        isResetEnable = false
        binding.forceReset.alpha = 0.2f
        binding.forceReset.isEnabled = false
        nextQuestion = placeScores.place_score
        newGame()
    }

    private fun handleBackNavigation() {
        if (flagForAnswer) {
            findNavController().navigateUp()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        if (this::questions.isInitialized && questions.isNotEmpty() && n in questions.indices && placeScores.place_score > 0) {
            Prefer.setIntPreference(requireContext(), getSavedQuestionStateKey(), n)
        }
        syncProgressIfSignedIn()
        super.onStop()
    }

    override fun onDestroyView() {
        answerTimer?.cancel()
        answerTimer = null
        cancelGoalAnimation()
        adMob?.destroyLoading()
        mInterstitialAd = null
        mRewardedAd = null
        soundManager?.release()
        soundManager = null
        _binding = null
        super.onDestroyView()
    }

    override fun show(amount: Int) {
        if (_binding == null || !isAdded) return
        dbHelper.resetPlace(placeScores.place_score_answer, this, isResetEnable)
    }

    private fun syncProgressIfSignedIn() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        if (!isAdded) return

        CloudSyncManager.uploadLocalStats(requireContext(), user) { _, _ -> }
    }

    private fun getSavedQuestionStateKey(): String {
        val placeType = if (this::questions.isInitialized) {
            questions.firstOrNull()?.type
        } else {
            null
        }
        return "save_question_state_${categoryType ?: "unknown"}_${placeType ?: "unknown"}"
    }
}
