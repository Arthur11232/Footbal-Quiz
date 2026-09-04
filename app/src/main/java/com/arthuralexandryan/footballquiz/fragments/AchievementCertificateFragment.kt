package com.arthuralexandryan.footballquiz.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.data.repository.SettingsRepository
import com.arthuralexandryan.footballquiz.databinding.FragmentAchievementCertificateBinding
import com.arthuralexandryan.footballquiz.domain.progression.ProgressionStageId
import com.arthuralexandryan.footballquiz.presentation.achievement.AchievementCertificatePayload
import com.arthuralexandryan.footballquiz.presentation.achievement.CertificateTheme
import com.arthuralexandryan.footballquiz.presentation.achievement.CertificateThemeMapper
import com.arthuralexandryan.footballquiz.utils.SystemBarStyleHelper
import com.google.android.play.core.review.ReviewManagerFactory
import org.koin.android.ext.android.inject
import java.text.DateFormat
import java.util.Date

class AchievementCertificateFragment : Fragment() {

    private var _binding: FragmentAchievementCertificateBinding? = null
    private val binding get() = _binding!!
    private val settingsRepository: SettingsRepository by inject()
    private var currentTheme: CertificateTheme? = null
    private var isClosing: Boolean = false

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

        binding.btnCertificateContinue.setOnClickListener { closeWithOptionalReview() }
        binding.btnCertificateClose.setOnClickListener { closeWithOptionalReview() }

        currentTheme = CertificateThemeMapper.themeFor(
            ProgressionStageId.fromValue(payload.primaryStageId)
        )
        applyTheme(requireNotNull(currentTheme))
        render(payload)
    }

    override fun onResume() {
        super.onResume()
        currentTheme?.let(::applySystemBars)
    }

    private fun render(payload: AchievementCertificatePayload) {
        applyRewardState(payload)
        binding.tvCertificateStage.setText(payload.primaryStageTitleResId)
        binding.tvCertificateRecipient.text = if (payload.isPendingAuthClaim) {
            getString(R.string.certificate_pending_recipient)
        } else {
            payload.recipientName
        }
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
                itemView.findViewById<TextView>(R.id.tvCertificateStageName).apply {
                    setText(titleResId)
                    currentTheme?.let { theme ->
                        background = createRoundedRectBackground(
                            fillColor = ColorUtils.blendARGB(theme.cardFillColor, theme.accentColor, 0.18f),
                            strokeColor = withAlpha(theme.primaryTextColor, 0.22f),
                            cornerRadiusDp = 16f
                        )
                        setTextColor(theme.primaryTextColor)
                    }
                }
                binding.additionalStagesContainer.addView(itemView)
            }
        }
    }

    private fun applyRewardState(payload: AchievementCertificatePayload) {
        val isPendingAuthClaim = payload.isPendingAuthClaim
        binding.tvAchievementCaption.setText(
            if (isPendingAuthClaim) {
                R.string.certificate_pending_caption
            } else {
                requireNotNull(currentTheme).captionResId
            }
        )
        binding.tvCertificateHeadline.setText(
            if (isPendingAuthClaim) {
                R.string.certificate_pending_headline
            } else {
                R.string.certificate_headline
            }
        )
        binding.tvCertificateSubheadline.setText(
            if (isPendingAuthClaim) {
                R.string.certificate_pending_subheadline
            } else {
                requireNotNull(currentTheme).subheadlineResId
            }
        )
        binding.tvCertificateStatusNote.visibility = if (isPendingAuthClaim) View.VISIBLE else View.GONE
        if (isPendingAuthClaim) {
            binding.tvCertificateStatusNote.setText(R.string.certificate_pending_status_note)
        }
        binding.tvCertificateLabel.setText(
            if (isPendingAuthClaim) {
                R.string.certificate_pending_title
            } else {
                R.string.certificate_title
            }
        )
        binding.tvCertificateAwardedToLabel.setText(
            if (isPendingAuthClaim) {
                R.string.certificate_pending_awarded_to
            } else {
                R.string.certificate_awarded_to
            }
        )
        binding.tvCertificateAwardText.setText(
            if (isPendingAuthClaim) {
                R.string.certificate_pending_award_text
            } else {
                R.string.certificate_award_text
            }
        )
        binding.tvCertificateIssuedLabel.setText(
            if (isPendingAuthClaim) {
                R.string.certificate_pending_issued_on
            } else {
                R.string.certificate_issued_on
            }
        )
        binding.tvCertificateIdLabel.setText(
            if (isPendingAuthClaim) {
                R.string.certificate_pending_id_label
            } else {
                R.string.certificate_id_label
            }
        )
        binding.btnCertificateContinue.setText(
            if (isPendingAuthClaim) {
                R.string.certificate_pending_continue_button
            } else {
                R.string.certificate_continue_button
            }
        )
    }

    private fun closeWithOptionalReview() {
        if (isClosing) return
        isClosing = true
        binding.btnCertificateContinue.isEnabled = false
        binding.btnCertificateClose.isEnabled = false

        if (!shouldRequestAppReviewAfterCertificate()) {
            navigateUpSafely()
            return
        }

        val currentActivity = activity
        if (currentActivity == null) {
            navigateUpSafely()
            return
        }

        Log.d(REVIEW_LOG_TAG, "Requesting Play in-app review after certificate.")
        val reviewManager = ReviewManagerFactory.create(currentActivity)
        reviewManager.requestReviewFlow().addOnCompleteListener { request ->
            if (_binding == null || !isAdded) return@addOnCompleteListener
            if (!request.isSuccessful) {
                Log.e(REVIEW_LOG_TAG, "Certificate ReviewInfo request failed.", request.exception)
                navigateUpSafely()
                return@addOnCompleteListener
            }

            Log.d(REVIEW_LOG_TAG, "Certificate ReviewInfo request succeeded. Launching review flow.")
            reviewManager.launchReviewFlow(currentActivity, request.result)
                .addOnCompleteListener { launch ->
                    if (launch.isSuccessful) {
                        Log.d(REVIEW_LOG_TAG, "Certificate review flow completed.")
                    } else {
                        Log.e(REVIEW_LOG_TAG, "Certificate review flow failed.", launch.exception)
                    }
                    navigateUpSafely()
                }
        }
    }

    private fun shouldRequestAppReviewAfterCertificate(): Boolean {
        val promptCount = settingsRepository.getAppReviewPromptCount()
        if (promptCount >= MAX_APP_REVIEW_PROMPTS) return false

        val now = System.currentTimeMillis()
        val lastPromptedAt = settingsRepository.getAppReviewLastPromptedAt()
        if (lastPromptedAt > 0 && now - lastPromptedAt < APP_REVIEW_COOLDOWN_MILLIS) return false

        settingsRepository.setAppReviewPromptCount(promptCount + 1)
        settingsRepository.setAppReviewLastPromptedAt(now)
        settingsRepository.setAppReviewPositiveAnswers(0)
        return true
    }

    private fun navigateUpSafely() {
        if (_binding == null || !isAdded) return
        findNavController().navigateUp()
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

    private fun formatIssuedAt(issuedAtMillis: Long): String {
        return DateFormat.getDateInstance(DateFormat.LONG).format(Date(issuedAtMillis))
    }

    private fun applyTheme(theme: CertificateTheme) {
        val screenColor = ContextCompat.getColor(requireContext(), theme.screenColorResId)

        binding.root.setBackgroundColor(screenColor)
        binding.certificateOverlay.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                ColorUtils.blendARGB(screenColor, Color.WHITE, if (theme.isMajorAchievement) 0.06f else 0.03f),
                ColorUtils.blendARGB(screenColor, Color.BLACK, if (theme.isMajorAchievement) 0.58f else 0.45f)
            )
        )

        binding.btnCertificateClose.background = createRoundedRectBackground(
            fillColor = ColorUtils.blendARGB(screenColor, Color.WHITE, 0.10f),
            strokeColor = withAlpha(theme.primaryTextColor, 0.28f),
            cornerRadiusDp = 16f
        )
        ImageViewCompat.setImageTintList(
            binding.btnCertificateClose,
            ColorStateList.valueOf(theme.primaryTextColor)
        )
        ImageViewCompat.setImageTintList(
            binding.ivTrophy,
            ColorStateList.valueOf(theme.emphasisColor)
        )
        binding.ivTrophy.scaleX = if (theme.isMajorAchievement) 1.12f else 1f
        binding.ivTrophy.scaleY = if (theme.isMajorAchievement) 1.12f else 1f

        binding.tvAchievementCaption.setText(theme.captionResId)
        binding.tvAchievementCaption.setTextColor(withAlpha(theme.primaryTextColor, 0.76f))
        binding.tvCertificateHeadline.setTextColor(theme.primaryTextColor)
        binding.tvCertificateSubheadline.setTextColor(theme.secondaryTextColor)
        binding.tvCertificateStatusNote.setTextColor(withAlpha(theme.primaryTextColor, 0.86f))
        binding.tvCertificateStatusNote.background = createRoundedRectBackground(
            fillColor = ColorUtils.blendARGB(theme.cardFillColor, theme.accentColor, 0.16f),
            strokeColor = withAlpha(theme.primaryTextColor, 0.14f),
            cornerRadiusDp = 16f
        )
        binding.tvCertificateHeadline.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (theme.isMajorAchievement) 36f else 34f
        )
        binding.tvCertificateStage.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (theme.isMajorAchievement) 18f else 17f
        )

        binding.certificateCard.background = createRoundedRectBackground(
            fillColor = theme.cardFillColor,
            strokeColor = theme.cardStrokeColor,
            cornerRadiusDp = 28f,
            strokeWidthDp = if (theme.isMajorAchievement) 2.5f else 1.5f
        )

        applyLineColor(binding.cardOrnamentHeaderLineStart, theme.accentColor, 0.40f)
        applyLineColor(binding.cardOrnamentHeaderLineEnd, theme.accentColor, 0.40f)
        applyLineColor(binding.cardOrnamentFooterLineStart, theme.accentColor, 0.40f)
        applyLineColor(binding.cardOrnamentFooterLineEnd, theme.accentColor, 0.40f)
        binding.cardOrnamentHeaderDiamond.setBackgroundColor(theme.accentColor)
        binding.cardOrnamentFooterDiamond.setBackgroundColor(theme.accentColor)
        binding.certificateDivider.setBackgroundColor(theme.accentColor)
        binding.metaDivider.setBackgroundColor(withAlpha(theme.primaryTextColor, 0.24f))
        binding.signatureDivider.setBackgroundColor(withAlpha(theme.accentColor, 0.25f))

        binding.tvCertificateLabel.setTextColor(withAlpha(theme.primaryTextColor, 0.68f))
        binding.tvCertificateRecipient.setTextColor(theme.primaryTextColor)
        binding.tvCertificateAwardedToLabel.setTextColor(theme.secondaryTextColor)
        binding.tvCertificateAwardText.setTextColor(withAlpha(theme.secondaryTextColor, 0.90f))

        binding.tvCertificateStage.background = createRoundedRectBackground(
            fillColor = theme.stageChipFillColor,
            strokeColor = withAlpha(theme.primaryTextColor, 0.10f),
            cornerRadiusDp = 18f
        )
        binding.tvCertificateStage.setTextColor(theme.stageChipTextColor)
        binding.tvAdditionalStages.setTextColor(theme.emphasisColor)

        binding.tvCertificateIssuedLabel.setTextColor(withAlpha(theme.primaryTextColor, 0.64f))
        binding.tvCertificateIdLabel.setTextColor(withAlpha(theme.primaryTextColor, 0.64f))
        binding.tvCertificateIssuedAt.setTextColor(theme.primaryTextColor)
        binding.tvCertificateId.setTextColor(theme.primaryTextColor)
        binding.tvCertificateSignatureLabel.setTextColor(withAlpha(theme.primaryTextColor, 0.64f))
        binding.tvCertificateSignature.setTextColor(theme.emphasisColor)
        binding.tvCertificateSignatureRole.setTextColor(withAlpha(theme.secondaryTextColor, 0.86f))

        binding.btnCertificateContinue.background = createButtonBackground(
            fillColor = theme.buttonFillColor,
            strokeColor = ColorUtils.blendARGB(
                theme.buttonFillColor,
                theme.accentColor,
                if (theme.isMajorAchievement) 0.45f else 0.30f
            )
        )
        binding.btnCertificateContinue.setTextColor(theme.buttonTextColor)

        applySystemBars(theme)
    }

    private fun applySystemBars(theme: CertificateTheme) {
        SystemBarStyleHelper.applySolidColor(
            fragment = this,
            color = ContextCompat.getColor(requireContext(), theme.screenColorResId),
            lightSystemBarIcons = false
        )
    }

    private fun applyLineColor(view: View, color: Int, alpha: Float) {
        view.setBackgroundColor(withAlpha(color, alpha))
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

    private companion object {
        const val REVIEW_LOG_TAG = "FQ_Review"
        const val MAX_APP_REVIEW_PROMPTS = 3
        const val APP_REVIEW_COOLDOWN_MILLIS = 14L * 24L * 60L * 60L * 1000L
    }
}
