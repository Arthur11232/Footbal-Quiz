package com.arthuralexandryan.footballquiz.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.databinding.FragmentAchievementCongratulationsBinding
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.presentation.achievement.AchievementCertificatePayload
import com.arthuralexandryan.footballquiz.presentation.achievement.CertificateTheme
import com.arthuralexandryan.footballquiz.presentation.achievement.CertificateThemeMapper
import com.arthuralexandryan.footballquiz.utils.SystemBarStyleHelper

class AchievementCongratulationsFragment : Fragment() {

    private var _binding: FragmentAchievementCongratulationsBinding? = null
    private val binding get() = _binding!!
    private var currentTheme: CertificateTheme? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievementCongratulationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val payload = readPayload() ?: run {
            findNavController().navigateUp()
            return
        }

        currentTheme = CertificateThemeMapper.themeFor(
            ProgressionStageId.fromValue(payload.primaryStageId)
        )
        applyTheme(requireNotNull(currentTheme))
        render(payload)

        binding.btnRewardClose.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnRewardContinue.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnRewardViewCertificate.setOnClickListener {
            findNavController().navigate(
                R.id.action_achievement_congratulations_to_achievement_certificate,
                Bundle().apply {
                    putParcelable("certificatePayload", payload)
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        currentTheme?.let(::applySystemBars)
    }

    private fun render(payload: AchievementCertificatePayload) {
        binding.tvRewardStage.setText(payload.primaryStageTitleResId)
        binding.tvRewardRecipient.text = if (payload.isPendingAuthClaim) {
            getString(R.string.congratulations_guest_recipient)
        } else {
            payload.recipientName
        }
        binding.tvRewardCaption.setText(
            if (payload.isPendingAuthClaim) {
                R.string.congratulations_caption_pending
            } else {
                R.string.congratulations_caption_awarded
            }
        )
        binding.tvRewardHeadline.setText(
            if (payload.isPendingAuthClaim) {
                R.string.congratulations_headline_pending
            } else {
                R.string.congratulations_headline_awarded
            }
        )
        binding.tvRewardBody.setText(
            if (payload.isPendingAuthClaim) {
                R.string.congratulations_body_pending
            } else {
                R.string.congratulations_body_awarded
            }
        )
        binding.tvRewardStatusNote.visibility = if (payload.isPendingAuthClaim) View.VISIBLE else View.GONE
        binding.tvRewardStatusNote.setText(
            if (payload.isPendingAuthClaim) {
                R.string.congratulations_pending_note
            } else {
                R.string.congratulations_awarded_note
            }
        )
        binding.btnRewardViewCertificate.setText(
            if (payload.isPendingAuthClaim) {
                R.string.congratulations_cta_view_reward
            } else {
                R.string.congratulations_cta_view_certificate
            }
        )
    }

    private fun readPayload(): AchievementCertificatePayload? {
        val args = arguments ?: return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args.getParcelable("certificatePayload", AchievementCertificatePayload::class.java)
        } else {
            @Suppress("DEPRECATION")
            args.getParcelable("certificatePayload")
        }
    }

    private fun applyTheme(theme: CertificateTheme) {
        val screenColor = ContextCompat.getColor(requireContext(), theme.screenColorResId)

        binding.root.setBackgroundColor(screenColor)
        binding.rewardOverlay.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                ColorUtils.blendARGB(screenColor, Color.WHITE, if (theme.isMajorAchievement) 0.06f else 0.03f),
                ColorUtils.blendARGB(screenColor, Color.BLACK, if (theme.isMajorAchievement) 0.52f else 0.42f)
            )
        )

        binding.btnRewardClose.background = createRoundedRectBackground(
            fillColor = ColorUtils.blendARGB(screenColor, Color.WHITE, 0.10f),
            strokeColor = withAlpha(theme.primaryTextColor, 0.28f),
            cornerRadiusDp = 16f
        )
        ImageViewCompat.setImageTintList(
            binding.btnRewardClose,
            ColorStateList.valueOf(theme.primaryTextColor)
        )
        ImageViewCompat.setImageTintList(
            binding.ivRewardTrophy,
            ColorStateList.valueOf(theme.emphasisColor)
        )

        binding.rewardCard.background = createRoundedRectBackground(
            fillColor = ColorUtils.blendARGB(theme.cardFillColor, Color.WHITE, 0.03f),
            strokeColor = theme.cardStrokeColor,
            cornerRadiusDp = 28f,
            strokeWidthDp = if (theme.isMajorAchievement) 2.5f else 1.5f
        )
        binding.tvRewardCaption.setTextColor(withAlpha(theme.primaryTextColor, 0.72f))
        binding.tvRewardHeadline.setTextColor(theme.primaryTextColor)
        binding.tvRewardBody.setTextColor(theme.secondaryTextColor)
        binding.tvRewardRecipientLabel.setTextColor(withAlpha(theme.primaryTextColor, 0.64f))
        binding.tvRewardRecipient.setTextColor(theme.primaryTextColor)
        binding.tvRewardStatusNote.setTextColor(theme.primaryTextColor)
        binding.tvRewardStatusNote.background = createRoundedRectBackground(
            fillColor = ColorUtils.blendARGB(theme.cardFillColor, theme.accentColor, 0.16f),
            strokeColor = withAlpha(theme.primaryTextColor, 0.14f),
            cornerRadiusDp = 16f
        )
        binding.tvRewardStage.setTextColor(theme.stageChipTextColor)
        binding.tvRewardStage.background = createRoundedRectBackground(
            fillColor = theme.stageChipFillColor,
            strokeColor = withAlpha(theme.primaryTextColor, 0.10f),
            cornerRadiusDp = 18f
        )
        binding.btnRewardViewCertificate.background = createButtonBackground(
            fillColor = theme.buttonFillColor,
            strokeColor = ColorUtils.blendARGB(theme.buttonFillColor, theme.accentColor, 0.30f)
        )
        binding.btnRewardViewCertificate.setTextColor(theme.buttonTextColor)
        binding.btnRewardContinue.background = createRoundedRectBackground(
            fillColor = ColorUtils.blendARGB(theme.cardFillColor, Color.WHITE, 0.04f),
            strokeColor = withAlpha(theme.primaryTextColor, 0.16f),
            cornerRadiusDp = 18f
        )
        binding.btnRewardContinue.setTextColor(theme.primaryTextColor)
        binding.tvRewardHeadline.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (theme.isMajorAchievement) 34f else 32f
        )

        applySystemBars(theme)
    }

    private fun applySystemBars(theme: CertificateTheme) {
        SystemBarStyleHelper.applySolidColor(
            fragment = this,
            color = ContextCompat.getColor(requireContext(), theme.screenColorResId),
            lightSystemBarIcons = false
        )
    }

    private fun createButtonBackground(fillColor: Int, strokeColor: Int): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                fillColor,
                ColorUtils.blendARGB(fillColor, Color.WHITE, 0.08f)
            )
        ).apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 20f.dp()
            setStroke(1f.dpInt(), strokeColor)
        }
    }

    private fun createRoundedRectBackground(
        fillColor: Int,
        strokeColor: Int,
        cornerRadiusDp: Float,
        strokeWidthDp: Float = 1f
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = cornerRadiusDp.dp()
            setColor(fillColor)
            setStroke(strokeWidthDp.dpInt(), strokeColor)
        }
    }

    private fun withAlpha(color: Int, alpha: Float): Int {
        return ColorUtils.setAlphaComponent(
            color,
            (alpha.coerceIn(0f, 1f) * 255).toInt()
        )
    }

    private fun Float.dp(): Float = this * resources.displayMetrics.density

    private fun Float.dpInt(): Int = dp().toInt().coerceAtLeast(1)

    override fun onDestroyView() {
        super.onDestroyView()
        currentTheme = null
        _binding = null
    }
}
