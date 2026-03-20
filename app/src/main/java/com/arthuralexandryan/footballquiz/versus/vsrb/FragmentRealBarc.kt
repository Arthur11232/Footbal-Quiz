package com.arthuralexandryan.footballquiz.versus.vsrb

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.constants.Constant
import com.arthuralexandryan.footballquiz.models.PlaceModel
import com.arthuralexandryan.footballquiz.models.PlaceModelInterface
import com.arthuralexandryan.footballquiz.versus.OnCheckScoreboard
import com.arthuralexandryan.footballquiz.versus.VS_ScoreboardModel

class FragmentRealBarc : Fragment(), OnCheckScoreboard, PlaceModelInterface {

    private lateinit var place_question: TextView
    private lateinit var place_answer: TextView
    private lateinit var category_score: TextView
    private lateinit var category_answer: TextView
    private lateinit var presenter: VSRB_ScoreboardPresenter
    private lateinit var root: View
    private var isActive = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_vs_rb, null)
        root = view.rootView
        initScoreboard(view)
        initImages(view)
        return view
    }

    @SuppressLint("SetTextI18n")
    private fun initLockScreen(presenter: VSRB_ScoreboardPresenter) {
        val lockScreen = root.findViewById<View>(R.id.lock_screen)
        lockScreen.visibility = View.VISIBLE
        lockScreen.findViewById<SeekBar>(R.id.seekBar).progress = presenter.getTotalScores()
        lockScreen.findViewById<TextView>(R.id.current_coins).text = presenter.getTotalScoresText()
        lockScreen.findViewById<TextView>(R.id.needed_coins).text = "${Constant.FOR_RB_SCORE}"
    }

    override fun onStart() {
        presenter = VSRB_ScoreboardPresenter(requireActivity(), this)
        if (!presenter.isReadyToPlay()) {
            isActive = false
            initLockScreen(presenter)
        } else {
            isActive = true
            root.findViewById<View>(R.id.scoreboard).visibility = View.VISIBLE
        }
        super.onStart()
    }

    private fun initImages(view: View) {
        PlaceModel.PlaceModelBuilder(view.findViewById(R.id.real_madrid))
            .setImage(R.drawable.real_madrid_logo)
            .setVersusFirst("Real Madrid")
            .setListener(this)
            .build()

        PlaceModel.PlaceModelBuilder(view.findViewById(R.id.barcelona))
            .setImage(R.drawable.barcelona_logo)
            .setVersusSecond("Barcelona")
            .setListener(this)
            .build()

        view.findViewById<View>(R.id.onBack).setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initScoreboard(view: View) {
        val scoreboard = view.findViewById<View>(R.id.scoreboard)
        place_answer = scoreboard.findViewById(R.id.place_answer)
        place_question = scoreboard.findViewById(R.id.place_question)
        category_answer = scoreboard.findViewById(R.id.category_answers)
        category_score = scoreboard.findViewById(R.id.category_score)
    }

    override fun onCheckScores(model: VS_ScoreboardModel) {
        place_answer.text = model.placeAnswer
        place_question.text = model.placeQuest
        category_answer.text = model.catAnswer
        category_score.text = model.catScore
    }

    override fun onClickPlace() {
        if (isActive) presenter.onButtonClick()
    }
}
