package com.arthuralexandryan.footballquiz.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
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

    private lateinit var adMob: AdBanner
    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedAd: RewardedAd? = null
    private var counter: Int = 0
    private var forceReloadBarrier: Int = 20
    private lateinit var adMobPresenter: AdMobPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActivityPlayNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adMobPresenter = AdMobPresenter(this)
        adMob = AdBanner(requireActivity(), binding.FQBannerAdView as android.view.ViewGroup, getString(R.string.banner_ad_unit_id))
        
        loadInterstitialAd()
        loadRewardedAd()

        arguments?.let {
            processArguments(it)
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(), getString(R.string.interstitial_ad_unit_id), adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            mInterstitialAd = null
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
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(requireContext(), getString(R.string.video_ad_unit_id), adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    mRewardedAd = rewardedAd
                    mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            mRewardedAd = null
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
            if (mRewardedAd != null) {
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
        if (placeScores.place_score > 0) {
            answers = mutableListOf()
            val random = Random()
            while (true) {
                val savedState = Prefer.getIntPreference(requireContext(), "save_question_state", -1)
                if (savedState < 0) {
                    n = random.nextInt(questions.size)
                } else {
                    n = savedState
                    Prefer.setIntPreference(requireContext(), "save_question_state", -1)
                    if (n >= questions.size) {
                        n = random.nextInt(questions.size)
                    }
                }
                if (!questions[n].isAnswered) {
                    setAnswers()
                    break
                }
            }
        } else {
            showFinishPage()
        }
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
            val x = Random().nextInt(answers.size)
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
        object : CountDownTimer(answerDelay, 1600) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished >= 1600) {
                    checkingAnswer(answer)
                    counter++
                }
            }

            override fun onFinish() {
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
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(requireActivity())
                    } else {
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
            binding.scoreboard.categoryAnswers.text = categoryScores.placeScoreAnswerText
            binding.scoreboard.placeAnswer.text = placeScores.placeScoreAnswerText
            dbHelper.setCategoryScores()
        } else {
            binding.answerIsCorrect.setText(R.string.answer_incorect)
            binding.answerIsCorrect.setBackgroundResource(R.drawable.toast_shape_false)
            binding.answerIsCorrect.visibility = View.VISIBLE
        }
        setAnswered()
        placeScores.place_score = placeScores.place_score - 1
        binding.scoreboard.placeQuestion.text = placeScores.placeScoreText
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
            if (mRewardedAd != null) {
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
    override fun reset(model: ScoreboardModel, isForce: Boolean) {
        questions.forEach { it.isAnswered = false }
        placeScores.place_score = model.placeScore
        placeScores.place_score_answer = model.placeAnswered
        categoryScores.place_score_answer = model.categoryAnswered
        
        binding.scoreboard.placeQuestion.text = "" + model.placeScore
        binding.scoreboard.placeAnswer.text = "" + model.placeAnswered
        binding.scoreboard.categoryAnswers.text = "" + model.categoryAnswered
        binding.scoreboard.categoryScore.text = "" + model.categoryScore
        binding.questionNumber.text = "" + model.placeScore
        
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
        Prefer.setIntPreference(requireContext(), "save_question_state", n)
        syncProgressIfSignedIn()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adMob.destroyLoading()
        _binding = null
    }

    override fun show(amount: Int) {
        dbHelper.resetPlace(placeScores.place_score_answer, this, isResetEnable)
    }

    private fun syncProgressIfSignedIn() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        if (!isAdded) return

        CloudSyncManager.uploadLocalStats(requireContext(), user) { _, _ -> }
    }
}
