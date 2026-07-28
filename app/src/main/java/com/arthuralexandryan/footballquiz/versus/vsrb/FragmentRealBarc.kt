package com.arthuralexandryan.footballquiz.versus.vsrb

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.data.repository.GameCatalogRepository
import com.arthuralexandryan.footballquiz.databinding.FragmentVsRbBinding
import com.arthuralexandryan.footballquiz.models.PlaceModel
import com.arthuralexandryan.footballquiz.models.PlaceModelInterface
import com.arthuralexandryan.footballquiz.utils.SystemBarStyleHelper
import com.arthuralexandryan.footballquiz.versus.OnCheckScoreboard
import com.arthuralexandryan.footballquiz.versus.VS_ScoreboardModel
import kotlin.math.min
import org.koin.android.ext.android.inject

class FragmentRealBarc : Fragment(), OnCheckScoreboard, PlaceModelInterface {

    private val gameCatalogRepository: GameCatalogRepository by inject()
    private lateinit var binding: FragmentVsRbBinding
    private lateinit var presenter: VSRB_ScoreboardPresenter
    private var isActive = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVsRbBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImages()
        applyInsets()
    }

    @SuppressLint("SetTextI18n")
    private fun initLockScreen(presenter: VSRB_ScoreboardPresenter) {
        binding.lockScreen.root.visibility = View.VISIBLE
        binding.scoreboard.root.visibility = View.INVISIBLE
        binding.lockScreen.seekBar.max = Constant.FOR_RB_SCORE
        binding.lockScreen.seekBar.progress = min(presenter.getTotalScores(), Constant.FOR_RB_SCORE)
        binding.lockScreen.seekBar.setOnTouchListener { _, event ->
            event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE ||
                event.action == MotionEvent.ACTION_UP
        }
        binding.lockScreen.currentCoins.text = presenter.getTotalScoresText()
        binding.lockScreen.neededCoins.text = Constant.FOR_RB_SCORE.toString()
    }

    override fun onStart() {
        presenter = VSRB_ScoreboardPresenter(requireActivity(), this)
        if (!presenter.isReadyToPlay()) {
            isActive = false
            initLockScreen(presenter)
        } else {
            isActive = true
            binding.lockScreen.root.visibility = View.GONE
            binding.scoreboard.root.visibility = View.VISIBLE
        }
        super.onStart()
    }

    private fun initImages() {
        PlaceModel.PlaceModelBuilder(binding.realMadrid.root)
            .setImage(R.drawable.real_madrid_logo)
            .setVersusFirst("Real Madrid")
            .setListener(this)
            .build()

        PlaceModel.PlaceModelBuilder(binding.barcelona.root)
            .setImage(R.drawable.barcelona_logo)
            .setVersusSecond("Barcelona")
            .setListener(this)
            .build()

        binding.onBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun applyInsets() {
        SystemBarStyleHelper.applySystemBarPadding(
            view = binding.scoreboard.root,
            applyTop = true
        )
        SystemBarStyleHelper.applySystemBarPadding(
            view = binding.onBack,
            applyLeft = true,
            applyTop = true
        )
    }

    override fun onCheckScores(model: VS_ScoreboardModel) {
        binding.scoreboard.placeAnswer.text = model.placeAnswer
        binding.scoreboard.placeQuestion.text = model.placeQuest
        binding.scoreboard.categoryAnswers.text = model.catAnswer
        binding.scoreboard.categoryScore.text = model.catScore
    }

    override fun onClickPlace() {
        if (isActive) {
            startVersusGame("vsRB")
        }
    }

    private fun startVersusGame(entryKey: String) {
        val launchData = gameCatalogRepository.getLaunchData(entryKey)
        val args = Bundle().apply {
            putParcelableArray("harcer", launchData.questions.toTypedArray())
            putParcelable("achokner", launchData.placeScores)
            putParcelable("cat_score", gameCatalogRepository.getCategoryScore(entryKey))
            putString("title", getString(R.string.versus))
            putString("category", entryKey)
        }
        findNavController().navigate(R.id.action_versus_to_play, args)
    }
}
