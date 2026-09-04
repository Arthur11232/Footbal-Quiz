package com.arthuralexandryan.footballquiz.versus.vsrb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.databinding.FragmentVsRbBinding
import com.arthuralexandryan.footballquiz.models.PlaceModel
import com.arthuralexandryan.footballquiz.models.PlaceModelInterface
import com.arthuralexandryan.footballquiz.presentation.versus.VersusEntryUiState
import com.arthuralexandryan.footballquiz.presentation.versus.VersusEntryViewModel
import com.arthuralexandryan.footballquiz.utils.SystemBarStyleHelper
import kotlinx.coroutines.launch
import kotlin.math.min
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentRealBarc : Fragment(), PlaceModelInterface {

    private val versusEntryViewModel: VersusEntryViewModel by viewModel()
    private lateinit var binding: FragmentVsRbBinding
    private var isActive = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVsRbBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImages()
        applyInsets()
        observeUiState()
        versusEntryViewModel.load(VersusEntryViewModel.ENTRY_REAL_BARCELONA)
    }

    private fun initLockScreen(state: VersusEntryUiState) = binding.run {
        lockScreen.root.visibility = View.VISIBLE
        scoreboard.root.visibility = View.INVISIBLE
        lockScreen.seekBar.max = state.unlockThreshold
        lockScreen.seekBar.progress = min(state.totalScore, state.unlockThreshold)
        lockScreen.seekBar.setOnTouchListener { _, event ->
            event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE ||
                event.action == MotionEvent.ACTION_UP
        }
        lockScreen.currentCoins.text = state.totalScoreText
        lockScreen.neededCoins.text = state.unlockThreshold.toString()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                versusEntryViewModel.uiState.collect(::renderUiState)
            }
        }
    }

    private fun renderUiState(state: VersusEntryUiState) {
        if (!state.isLoaded) return

        isActive = state.isReady
        renderScoreboard(state)

        if (state.isReady) binding.run {
            lockScreen.root.visibility = View.GONE
            scoreboard.root.visibility = View.VISIBLE
        } else {
            initLockScreen(state)
        }
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

    private fun renderScoreboard(state: VersusEntryUiState) = binding.scoreboard.run {
        placeAnswer.text = state.placeAnswerText
        placeQuestion.text = state.placeQuestionText
        categoryAnswers.text = state.categoryAnswerText
        categoryScore.text = state.categoryScoreText
    }

    override fun onClickPlace() {
        if (isActive) {
            startVersusGame(VersusEntryViewModel.ENTRY_REAL_BARCELONA)
        }
    }

    private fun startVersusGame(entryKey: String) {
        val launchData = versusEntryViewModel.getLaunchData(entryKey)
        val args = Bundle().apply {
            putParcelableArray("harcer", launchData.questions.toTypedArray())
            putParcelable("achokner", launchData.placeScores)
            putParcelable("cat_score", versusEntryViewModel.getCategoryScore(entryKey))
            putString("title", getString(R.string.versus))
            putString("category", entryKey)
        }
        findNavController().navigate(R.id.action_versus_to_play, args)
    }
}
