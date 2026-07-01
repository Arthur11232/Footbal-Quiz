package com.arthuralexandryan.footballquiz.fragments

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.databinding.FragmentProfileBinding
import com.arthuralexandryan.footballquiz.models.UserStatsDTO
import com.arthuralexandryan.footballquiz.presentation.profile.ProfileEffect
import com.arthuralexandryan.footballquiz.presentation.profile.ProfileCertificateUiModel
import com.arthuralexandryan.footballquiz.presentation.profile.ProfileUiState
import com.arthuralexandryan.footballquiz.presentation.profile.ProfileViewModel
import com.arthuralexandryan.footballquiz.presentation.progression.CompletedStageUiModel
import com.arthuralexandryan.footballquiz.utils.DialogManager
import com.arthuralexandryan.footballquiz.utils.SystemBarStyleHelper
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModel()
    private var isBindingNotificationSwitches: Boolean = false

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { profileViewModel.onPhotoSelected(it) }
    }
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (_binding == null) return@registerForActivityResult
            profileViewModel.onNotificationPermissionResult(granted)
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUiState()
        observeEffects()

        binding.btnSignOut.setOnClickListener { profileViewModel.onSignOutClicked() }
        binding.btnDeleteAccount.setOnClickListener { profileViewModel.onDeleteAccountClicked() }
        binding.btnEditPhoto.setOnClickListener { imagePickerLauncher.launch("image/*") }
        binding.btnRestoreCloud.setOnClickListener { profileViewModel.onRestoreCloudClicked() }
        binding.btnOverwriteCloud.setOnClickListener { profileViewModel.onOverwriteCloudClicked() }
        binding.onBack.setOnClickListener { findNavController().navigateUp() }
        initNotificationListeners()

        profileViewModel.loadProfile()
    }

    override fun onResume() {
        super.onResume()
        SystemBarStyleHelper.applySampledDrawable(
            fragment = this,
            drawableResId = R.drawable.football,
            matchNavigationToStatus = false,
            lightSystemBarIcons = false
        )
        profileViewModel.loadProfile()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                profileViewModel.uiState.collect { state ->
                    if (_binding == null) return@collect
                    render(state)
                }
            }
        }
    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                profileViewModel.effects.collect { effect ->
                    when (effect) {
                        is ProfileEffect.ShowToastRes -> {
                            Toast.makeText(requireContext(), getString(effect.messageResId), Toast.LENGTH_LONG).show()
                        }
                        is ProfileEffect.ShowToastText -> {
                            Toast.makeText(requireContext(), effect.message, Toast.LENGTH_LONG).show()
                        }
                        is ProfileEffect.ShowRestoreDialog -> {
                            showRestoreDialog(effect.cloudStats)
                        }
                        is ProfileEffect.ShowOverwriteDialog -> {
                            showOverwriteCloudDialog(effect.localScore, effect.localAnswered)
                        }
                        is ProfileEffect.OpenCertificatePreview -> {
                            openCertificatePreview(effect.payload)
                        }
                        ProfileEffect.RequestNotificationPermission -> {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        ProfileEffect.ShowDeleteAccountDialog -> {
                            showDeleteAccountDialog()
                        }
                        ProfileEffect.NavigateBackToStart -> {
                            navigateBackToStartPage()
                        }
                    }
                }
            }
        }
    }

    private fun initNotificationListeners() = binding.run {
        switchNotificationsAll.setOnCheckedChangeListener { _, isChecked ->
            if (isBindingNotificationSwitches) return@setOnCheckedChangeListener
            profileViewModel.onNotificationsEnabledChanged(isChecked)
        }
        switchNotificationsProgress.setOnCheckedChangeListener { _, isChecked ->
            if (isBindingNotificationSwitches) return@setOnCheckedChangeListener
            profileViewModel.onProgressNotificationsEnabledChanged(isChecked)
        }
        switchNotificationsRewards.setOnCheckedChangeListener { _, isChecked ->
            if (isBindingNotificationSwitches) return@setOnCheckedChangeListener
            profileViewModel.onRewardNotificationsEnabledChanged(isChecked)
        }
        switchNotificationsUpdates.setOnCheckedChangeListener { _, isChecked ->
            if (isBindingNotificationSwitches) return@setOnCheckedChangeListener
            profileViewModel.onUpdateNotificationsEnabledChanged(isChecked)
        }
    }

    private fun render(state: ProfileUiState) {
        binding.uploadProgress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.btnSignOut.isEnabled = !state.isLoading
        binding.btnEditPhoto.isEnabled = !state.isLoading
        binding.btnRestoreCloud.isEnabled = !state.isLoading
        binding.btnOverwriteCloud.isEnabled = !state.isLoading
        binding.btnDeleteAccount.isEnabled = !state.isLoading

        if (state.isSignedIn) {
            binding.userName.text = state.userName.ifEmpty { getString(R.string.profile_guest) }
            binding.userEmail.text = state.userEmail.ifEmpty { getString(R.string.profile_not_signed_in) }
            binding.btnSignOut.visibility = View.VISIBLE
            binding.btnEditPhoto.visibility = View.VISIBLE
            binding.btnSync.visibility = View.GONE
            binding.btnRestoreCloud.visibility = View.VISIBLE
            binding.btnOverwriteCloud.visibility = View.VISIBLE
            binding.btnDeleteAccount.visibility = View.VISIBLE
        } else {
            binding.userName.text = getString(R.string.profile_guest)
            binding.userEmail.text = getString(R.string.profile_not_signed_in)
            binding.btnSignOut.visibility = View.GONE
            binding.btnEditPhoto.visibility = View.GONE
            binding.btnSync.visibility = View.GONE
            binding.btnRestoreCloud.visibility = View.GONE
            binding.btnOverwriteCloud.visibility = View.GONE
            binding.btnDeleteAccount.visibility = View.GONE
        }

        renderNotificationSettings(state)

        val localPhotoPath = state.localPhotoPath
        val imageSource = when {
            localPhotoPath != null && File(localPhotoPath).exists() -> localPhotoPath
            !state.remotePhotoUrl.isNullOrEmpty() -> state.remotePhotoUrl
            else -> R.drawable.ronaldo
        }
        Glide.with(this)
            .load(imageSource)
            .placeholder(R.drawable.ronaldo)
            .error(R.drawable.ronaldo)
            .into(binding.userPhoto)

        binding.tvTotalScoreValue.text = state.totalScoreText
        binding.tvAnsweredCountValue.text = state.answeredCountText
        binding.tvTop5Progress.text = state.top5ProgressText
        binding.tvUefaProgress.text = state.uefaProgressText
        binding.tvWorldProgress.text = state.worldProgressText
        binding.tvVersusProgress.text = state.versusProgressText
        binding.tvCertificatesSummary.text = state.certificatesCount.toString()
        binding.tvCertificatesEmpty.visibility = if (state.certificates.isEmpty()) View.VISIBLE else View.GONE
        binding.certificatesScroll.visibility = if (state.certificates.isEmpty()) View.GONE else View.VISIBLE
        binding.tvCompletedStagesSummary.text = getString(
            R.string.progression_completed_stages_summary,
            state.completedStagesCount,
            state.totalStagesCount
        )
        binding.tvCompletedStagesEmpty.visibility = if (state.completedStages.isEmpty()) View.VISIBLE else View.GONE
        renderCertificates(state.certificates)
        renderCompletedStages(state.completedStages)
    }

    private fun renderNotificationSettings(state: ProfileUiState) {
        binding.run {
            cardNotifications.visibility = if (state.showNotificationSettings) View.VISIBLE else View.GONE
            if (!state.showNotificationSettings) return@run

            val childSwitchesEnabled = state.notificationsEnabled && !state.isLoading
            val permissionHintVisible = state.notificationsEnabled && !state.notificationPermissionGranted

            isBindingNotificationSwitches = true
            switchNotificationsAll.isChecked = state.notificationsEnabled
            switchNotificationsAll.isEnabled = !state.isLoading
            switchNotificationsProgress.isChecked = state.progressNotificationsEnabled
            switchNotificationsProgress.isEnabled = childSwitchesEnabled
            switchNotificationsRewards.isChecked = state.rewardNotificationsEnabled
            switchNotificationsRewards.isEnabled = childSwitchesEnabled
            switchNotificationsUpdates.isChecked = state.updateNotificationsEnabled
            switchNotificationsUpdates.isEnabled = childSwitchesEnabled
            isBindingNotificationSwitches = false

            tvNotificationsStatus.text = getString(
                if (!state.notificationsEnabled) {
                    R.string.profile_notifications_status_off
                } else if (state.notificationPermissionGranted) {
                    R.string.profile_notifications_status_on
                } else {
                    R.string.profile_notifications_status_permission_required
                }
            )
            tvNotificationPermissionHint.visibility = if (permissionHintVisible) View.VISIBLE else View.GONE
        }
    }

    private fun renderCertificates(certificates: List<ProfileCertificateUiModel>) {
        binding.certificatesContainer.removeAllViews()
        certificates.forEach { item ->
            val itemView = layoutInflater.inflate(
                R.layout.item_profile_certificate,
                binding.certificatesContainer,
                false
            )
            itemView.findViewById<TextView>(R.id.tvCertificateStage).setText(item.stageTitleResId)
            itemView.findViewById<TextView>(R.id.tvCertificateRecipient).text = item.recipientName
            itemView.findViewById<TextView>(R.id.tvCertificateIssuedAt).text = item.issuedAtText
            itemView.findViewById<TextView>(R.id.tvCertificateId).text = item.certificateId
            itemView.setOnClickListener {
                profileViewModel.onCertificateClicked(item)
            }
            binding.certificatesContainer.addView(itemView)
        }
    }

    private fun renderCompletedStages(completedStages: List<CompletedStageUiModel>) {
        binding.completedStagesContainer.removeAllViews()
        completedStages.forEach { item ->
            val itemView = layoutInflater.inflate(
                R.layout.item_profile_completed_stage,
                binding.completedStagesContainer,
                false
            )
            itemView.findViewById<android.widget.TextView>(R.id.tvCompletedStageTitle).setText(item.titleResId)
            itemView.findViewById<android.widget.TextView>(R.id.tvCompletedStageProgress).text = item.progressText
            binding.completedStagesContainer.addView(itemView)
        }
    }

    private fun showRestoreDialog(cloudStats: UserStatsDTO) {
        DialogManager.showActionDialog(
            context = requireContext(),
            inflater = layoutInflater,
            title = getString(R.string.profile_restore_title),
            message = getString(R.string.profile_manual_restore_message, cloudStats.gameState.total),
            primaryText = getString(R.string.profile_restore_confirm),
            secondaryText = getString(R.string.profile_restore_cancel),
            onPrimaryClick = {
                profileViewModel.onRestoreCloudConfirmed(cloudStats)
            }
        )
    }

    private fun openCertificatePreview(payload: com.arthuralexandryan.footballquiz.presentation.achievement.AchievementCertificatePayload) {
        findNavController().navigate(
            R.id.achievementCertificateFragment,
            Bundle().apply {
                putParcelable("certificatePayload", payload)
            }
        )
    }

    private fun showDeleteAccountDialog() {
        DialogManager.showActionDialog(
            context = requireContext(),
            inflater = layoutInflater,
            title = getString(R.string.profile_delete_title),
            message = getString(R.string.profile_delete_message),
            primaryText = getString(R.string.profile_delete_confirm),
            secondaryText = getString(R.string.profile_delete_cancel),
            onPrimaryClick = {
                profileViewModel.onDeleteAccountConfirmed()
            }
        )
    }

    private fun showOverwriteCloudDialog(localScore: Int, localAnswered: Int) {
        DialogManager.showActionDialog(
            context = requireContext(),
            inflater = layoutInflater,
            title = getString(R.string.profile_overwrite_cloud_title),
            message = getString(R.string.profile_overwrite_cloud_message, localScore, localAnswered),
            primaryText = getString(R.string.profile_sync_overwrite_button),
            secondaryText = getString(R.string.profile_restore_cancel),
            onPrimaryClick = {
                profileViewModel.onOverwriteCloudConfirmed()
            }
        )
    }

    private fun navigateBackToStartPage() {
        val navController = findNavController()
        val poppedToStart = navController.popBackStack(R.id.startPageFragment, false)
        if (!poppedToStart && navController.currentDestination?.id != R.id.startPageFragment) {
            navController.navigate(R.id.startPageFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
