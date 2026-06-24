package com.arthuralexandryan.footballquiz.versus.vsrm


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
import com.arthuralexandryan.footballquiz.databinding.FragmentVsRmBinding
import com.arthuralexandryan.footballquiz.models.PlaceModel
import com.arthuralexandryan.footballquiz.models.PlaceModelInterface
import com.arthuralexandryan.footballquiz.utils.SystemBarStyleHelper
import com.arthuralexandryan.footballquiz.versus.OnCheckScoreboard
import com.arthuralexandryan.footballquiz.versus.VS_ScoreboardModel
import kotlin.math.min
import org.koin.android.ext.android.inject

class FragmentRonMessi : Fragment(), OnCheckScoreboard, PlaceModelInterface {
    private lateinit var binding: FragmentVsRmBinding

    private val gameCatalogRepository: GameCatalogRepository by inject()
    private var presenter: VSRM_ScoreboardPresenter? = null
    private var isActive: Boolean = false

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentVsRmBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImages(view)
        applyInsets()

        presenter = activity?.let { VSRM_ScoreboardPresenter(it, this) }
        if (presenter?.isReadyToPlay() == false) {
            isActive = false
            initLockScreen(presenter!!)
        } else {
            isActive = true
            binding.lockScreen.root.visibility = View.GONE
            binding.scoreboard.root.visibility = View.VISIBLE
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

    private fun initLockScreen(presenter: VSRM_ScoreboardPresenter) {
        binding.lockScreen.root.visibility = View.VISIBLE
        binding.scoreboard.root.visibility = View.INVISIBLE
        binding.lockScreen.seekBar.max = Constant.FOR_RM_SCORE
        binding.lockScreen.seekBar.progress = min(presenter.getTotalScores(), Constant.FOR_RM_SCORE)
        binding.lockScreen.seekBar.setOnTouchListener { _, event ->
            event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE ||
                event.action == MotionEvent.ACTION_UP
        }
        binding.lockScreen.currentCoins.text = presenter.getTotalScoresText()
        binding.lockScreen.neededCoins.text = Constant.FOR_RM_SCORE.toString()
    }

    private fun initImages(view: View) {
        PlaceModel.PlaceModelBuilder(binding.ronaldo.root)
                .setListener(this)
                .setImage(R.drawable.ronaldo)
                .setVersusFirst("Ronaldo")
                .build()
        PlaceModel.PlaceModelBuilder(binding.messi.root)
                .setListener(this)
                .setImage(R.drawable.messi)
                .setVersusSecond("Messi")
                .build()
        binding.onBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCheckScores(model: VS_ScoreboardModel) {
        binding.scoreboard.placeAnswer.text = model.placeAnswer
        binding.scoreboard.placeQuestion.text = model.placeQuest
        binding.scoreboard.categoryAnswers.text = model.catAnswer
        binding.scoreboard.categoryScore.text = model.catScore
    }

    override fun onClickPlace() {
        if (isActive) {
            startVersusGame("vsRM")
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
