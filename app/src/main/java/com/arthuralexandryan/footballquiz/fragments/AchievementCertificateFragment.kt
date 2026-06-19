package com.arthuralexandryan.footballquiz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.databinding.FragmentAchievementCertificateBinding
import com.arthuralexandryan.footballquiz.presentation.achievement.AchievementCertificatePayload
import com.arthuralexandryan.footballquiz.utils.SystemBarStyleHelper
import java.text.DateFormat
import java.util.Date

class AchievementCertificateFragment : Fragment() {

    private var _binding: FragmentAchievementCertificateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievementCertificateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val payload = readPayload() ?: run {
            findNavController().navigateUp()
            return
        }

        binding.btnCertificateContinue.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnCertificateClose.setOnClickListener {
            findNavController().navigateUp()
        }

        render(payload)
    }

    override fun onResume() {
        super.onResume()
        SystemBarStyleHelper.applySampledDrawable(
            fragment = this,
            drawableResId = R.drawable.football,
            matchNavigationToStatus = false,
            lightSystemBarIcons = false
        )
    }

    private fun render(payload: AchievementCertificatePayload) {
        binding.tvCertificateStage.setText(payload.primaryStageTitleResId)
        binding.tvCertificateRecipient.text = payload.recipientName
        binding.tvCertificateIssuedAt.text = formatIssuedAt(payload.issuedAtMillis)
        binding.tvCertificateId.text = payload.certificateId

        val additionalStageIds = payload.additionalStageTitleResIds
        if (additionalStageIds.isEmpty()) {
            binding.tvAdditionalStages.visibility = View.GONE
            binding.additionalStagesContainer.visibility = View.GONE
        } else {
            binding.tvAdditionalStages.visibility = View.VISIBLE
            binding.additionalStagesContainer.visibility = View.VISIBLE
            binding.tvAdditionalStages.text = getString(
                R.string.certificate_additional_stages,
                additionalStageIds.size
            )
            binding.additionalStagesContainer.removeAllViews()
            additionalStageIds.forEach { titleResId ->
                val itemView = layoutInflater.inflate(
                    R.layout.item_certificate_stage,
                    binding.additionalStagesContainer,
                    false
                )
                itemView.findViewById<android.widget.TextView>(R.id.tvCertificateStageName)
                    .setText(titleResId)
                binding.additionalStagesContainer.addView(itemView)
            }
        }
    }

    private fun readPayload(): AchievementCertificatePayload? {
        @Suppress("DEPRECATION")
        return arguments?.getParcelable("certificatePayload")
    }

    private fun formatIssuedAt(issuedAtMillis: Long): String {
        return DateFormat.getDateInstance(DateFormat.LONG).format(Date(issuedAtMillis))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
