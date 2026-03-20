package com.arthuralexandryan.footballquiz.versus.vsrm


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.databinding.FragmentVsRmBinding
import com.arthuralexandryan.footballquiz.models.PlaceModel
import com.arthuralexandryan.footballquiz.models.PlaceModelInterface
import com.arthuralexandryan.footballquiz.versus.OnCheckScoreboard
import com.arthuralexandryan.footballquiz.versus.VS_ScoreboardModel

class FragmentRonMessi : Fragment(), OnCheckScoreboard, PlaceModelInterface {
    private lateinit var binding: FragmentVsRmBinding

    private var presenter: VSRM_ScoreboardPresenter? = null
    private lateinit var root: View
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

        presenter = activity?.let { VSRM_ScoreboardPresenter(it, this) }
        if (presenter?.isReadyToPlay() == false) {
            isActive = false
            initLockScreen(presenter!!)
        } else {
            isActive = true
             binding.scoreboard.root.visibility = View.VISIBLE
        }
    }

    override fun onStart() {

        super.onStart()
    }

    private fun initLockScreen(presenter: VSRM_ScoreboardPresenter) {
        binding.lockScreen.root.visibility = View.VISIBLE
        binding.lockScreen.seekBar.progress = presenter.getTotalScores()
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
            activity?.onBackPressed()
        }
    }

    override fun onCheckScores(model: VS_ScoreboardModel) {
        binding.scoreboard.placeAnswer.text = model.placeAnswer
        binding.scoreboard.placeQuestion.text = model.placeQuest
        binding.scoreboard.categoryAnswers.text = model.catAnswer
        binding.scoreboard.categoryScore.text = model.catScore
    }

    override fun onClickPlace() {
        if (isActive){
            presenter!!.onButtonClick()
        }
    }
}
