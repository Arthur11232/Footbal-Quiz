package com.arthuralexandryan.footballquiz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.databinding.LayoutVersusBinding
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.models.TarberakArrays

class VSFragment : Fragment() {

    private var _binding: LayoutVersusBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DB_Helper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutVersusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DB_Helper()

        initViews()
        inflateView()

        binding.onBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initViews() {
        binding.vs1.vsItem.setImageResource(TarberakArrays.rb_image[0])
        binding.vs2.vsItem.setImageResource(TarberakArrays.rb_image[1])
        binding.scoreboard.placeNameScores.text = getString(R.string.versus)
    }

    private fun inflateView() {
        val scoreboardModel = dbHelper.realBarcScoreBoard
        binding.scoreboard.placeQuestion.text = scoreboardModel.placeScore.toString()
        binding.scoreboard.placeAnswer.text = scoreboardModel.placeAnswered.toString()
        binding.scoreboard.categoryAnswers.text = scoreboardModel.categoryAnswered.toString()
        binding.scoreboard.categoryScore.text = scoreboardModel.categoryScore.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
