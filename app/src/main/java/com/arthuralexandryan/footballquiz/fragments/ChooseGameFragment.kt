package com.arthuralexandryan.footballquiz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.constants.Constant.FOR_UEFA_SCORE
import com.arthuralexandryan.footballquiz.constants.Constant.FOR_WORLD_SCORE
import com.arthuralexandryan.footballquiz.databinding.ActivityChooseGameBinding
import com.arthuralexandryan.footballquiz.databinding.ActivityChooseGameNewBinding
import com.arthuralexandryan.footballquiz.db_app.DB_Helper

class ChooseGameFragment : Fragment(), View.OnClickListener {

    private var _binding: ActivityChooseGameNewBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DB_Helper
    private var score: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityChooseGameNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DB_Helper()

        binding.onBack.setOnClickListener { findNavController().navigateUp() }

        binding.btnTop.setOnClickListener(this)
        binding.btnUefa.setOnClickListener(this)
        binding.btnChamp.setOnClickListener(this)
        binding.btnVersus.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        updateScores()
    }

    private fun updateScores() {
        score = db.top5AnsweredScores
        if (score < FOR_UEFA_SCORE) {
            binding.txtScoreUefa.text = getString(R.string.text_score_uefa, "$score")
            binding.txtScoreUefa.visibility = View.VISIBLE
        } else {
            binding.txtScoreUefa.visibility = View.GONE
        }

        val totalScore = db.top5AnsweredScores + db.getUFAAnsweredScores()
        if (totalScore < FOR_WORLD_SCORE) {
            binding.txtScoreWorld.text = getString(R.string.text_score_world, "$totalScore")
            binding.txtScoreWorld.visibility = View.VISIBLE
        } else {
            binding.txtScoreWorld.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        var isOpen = true
        val currentScore = db.top5AnsweredScores
        val totalScore = db.top5AnsweredScores + db.getUFAAnsweredScores()

        val args = Bundle()
        var destinationId = R.id.action_choose_to_category

        when (v.id) {
            R.id.btnTop -> {
                args.putString("gameScore", "top5")
            }
            R.id.btnUefa -> {
                if (currentScore >= FOR_UEFA_SCORE) {
                    args.putString("gameScore", "uefa")
                } else {
                    isOpen = false
                    Toast.makeText(requireContext(), getString(R.string.points_not_enough), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnChamp -> {
                if (totalScore >= FOR_WORLD_SCORE) {
                    args.putString("gameScore", "world")
                } else {
                    isOpen = false
                    Toast.makeText(requireContext(), getString(R.string.points_not_enough), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnVersus -> {
                destinationId = R.id.action_choose_to_versus
            }
        }

        if (isOpen) {
            findNavController().navigate(destinationId, args)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
