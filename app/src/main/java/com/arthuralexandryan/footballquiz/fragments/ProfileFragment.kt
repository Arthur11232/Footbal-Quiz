package com.arthuralexandryan.footballquiz.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.auth.AuthManager
import com.arthuralexandryan.footballquiz.databinding.FragmentProfileBinding
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.interfaces.Check
import com.arthuralexandryan.footballquiz.models.CloudSyncManager
import com.arthuralexandryan.footballquiz.models.UserStatsService
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.DialogManager
import com.arthuralexandryan.footballquiz.utils.Prefer
import com.arthuralexandryan.footballquiz.utils.SystemBarStyleHelper
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var authManager: AuthManager
    private lateinit var dbHelper: DB_Helper

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { saveImageLocally(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authManager = AuthManager(requireContext())
        dbHelper = DB_Helper(requireActivity())

        updateUI()
        loadStatistics()

        binding.btnSignOut.setOnClickListener {
            authManager.signOut {
                activity?.runOnUiThread {
                    updateUI()
                    loadStatistics()
                    navigateBackToStartPage()
                }
            }
        }

        binding.btnDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }

        binding.btnEditPhoto.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnRestoreCloud.setOnClickListener {
            restoreFromCloudManually()
        }

        binding.btnOverwriteCloud.setOnClickListener {
            showOverwriteCloudDialog()
        }

        binding.onBack.setOnClickListener { findNavController().navigateUp() }
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

    private fun updateUI() {
        if (_binding == null) return
        val user = authManager.getCurrentUser()
        
        // Priority: 
        // 1. Local saved photo path from Preferences
        // 2. Firebase/Google photo URL
        // 3. Default placeholder
        val localPhotoPath = com.arthuralexandryan.footballquiz.utils.Prefer.getStringPreference(
            requireContext(), 
            com.arthuralexandryan.footballquiz.utils.Constants.UserPhotoKey + (user?.uid ?: "guest"), 
            null
        )

        if (user != null) {
            binding.userName.text = user.displayName ?: getString(R.string.profile_guest)
            binding.userEmail.text = user.email ?: getString(R.string.profile_not_signed_in)
            binding.btnSignOut.visibility = View.VISIBLE
            binding.btnEditPhoto.visibility = View.VISIBLE
            binding.btnSync.visibility = View.GONE
            binding.btnRestoreCloud.visibility = View.VISIBLE
            binding.btnOverwriteCloud.visibility = View.VISIBLE
            binding.btnDeleteAccount.visibility = View.VISIBLE

            val imageSource = if (localPhotoPath != null && File(localPhotoPath).exists()) {
                localPhotoPath
            } else {
                user.photoUrl
            }

            Glide.with(this)
                .load(imageSource)
                .placeholder(R.drawable.ronaldo)
                .error(R.drawable.ronaldo)
                .into(binding.userPhoto)
        } else {
            binding.userName.text = getString(R.string.profile_guest)
            binding.userEmail.text = getString(R.string.profile_not_signed_in)
            binding.btnSignOut.visibility = View.GONE
            binding.btnEditPhoto.visibility = View.GONE
            binding.btnSync.visibility = View.GONE
            binding.btnRestoreCloud.visibility = View.GONE
            binding.btnOverwriteCloud.visibility = View.GONE
            binding.btnDeleteAccount.visibility = View.GONE
            
            if (localPhotoPath != null && File(localPhotoPath).exists()) {
                Glide.with(this).load(localPhotoPath).into(binding.userPhoto)
            } else {
                binding.userPhoto.setImageResource(R.drawable.ronaldo)
            }
        }
    }

    private fun saveImageLocally(uri: Uri) {
        val user = authManager.getCurrentUser()
        val userId = user?.uid ?: "guest"
        
        binding.uploadProgress.visibility = View.VISIBLE
        binding.btnEditPhoto.isEnabled = false

        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val fileName = "profile_$userId.jpg"
            val file = File(requireContext().filesDir, fileName)
            val outputStream = FileOutputStream(file)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            // Save the absolute path in Preferences
            Prefer.setStringPreference(
                requireContext(), 
                com.arthuralexandryan.footballquiz.utils.Constants.UserPhotoKey + userId, 
                file.absolutePath
            )

            Toast.makeText(context, getString(R.string.profile_edit_photo), Toast.LENGTH_SHORT).show()
            updateUI()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            binding.uploadProgress.visibility = View.GONE
            binding.btnEditPhoto.isEnabled = true
        }
    }

    private fun showRestoreDialog(cloudStats: com.arthuralexandryan.footballquiz.models.UserStatsDTO) {
        val context = context ?: return
        DialogManager.showActionDialog(
            context = context,
            inflater = layoutInflater,
            title = getString(R.string.profile_restore_title),
            message = getString(R.string.profile_manual_restore_message, cloudStats.gameState.total),
            primaryText = getString(R.string.profile_restore_confirm),
            secondaryText = getString(R.string.profile_restore_cancel),
            onPrimaryClick = {
                val userId = authManager.getCurrentUser()?.uid ?: return@showActionDialog
                CloudSyncManager.restoreCloudStats(requireContext(), dbHelper, userId, cloudStats)
                updateUI()
                loadStatistics()
                Toast.makeText(context, getString(R.string.profile_progress_restored), Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showDeleteAccountDialog() {
        val context = context ?: return
        DialogManager.showActionDialog(
            context = context,
            inflater = layoutInflater,
            title = getString(R.string.profile_delete_title),
            message = getString(R.string.profile_delete_message),
            primaryText = getString(R.string.profile_delete_confirm),
            secondaryText = getString(R.string.profile_delete_cancel),
            onPrimaryClick = {
                deleteAccountAndData()
            }
        )
    }

    private fun deleteAccountAndData() {
        val user = authManager.getCurrentUser() ?: return
        setDeleteInProgress(true)

        UserStatsService.getInstance().deleteStats(user.uid) { cloudDeleted ->
            activity?.runOnUiThread {
                if (!cloudDeleted) {
                    setDeleteInProgress(false)
                    Toast.makeText(context, getString(R.string.profile_delete_error), Toast.LENGTH_LONG).show()
                    return@runOnUiThread
                }

                clearLocalUserData(user.uid) {
                    deleteFirebaseAccount(user)
                }
            }
        }
    }

    private fun clearLocalUserData(userId: String, onComplete: () -> Unit) {
        val context = context ?: return
        val photoPath = Prefer.getStringPreference(context, Constants.UserPhotoKey + userId, null)
        if (photoPath != null) {
            File(photoPath).delete()
        }
        Prefer.getSharedPreferenceEditor(context)
            .remove(Constants.UserPhotoKey + userId)
            .remove(Constants.UserStatsLastSyncKeyPrefix + userId)
            .remove(Constants.UserStatsRestorePromptDismissedKeyPrefix + userId)
            .putBoolean("isFirstPlay", true)
            .apply()

        dbHelper.deleteAll(object : Check {
            override fun onCheck() {
                activity?.runOnUiThread {
                    onComplete()
                }
            }
        }) {
            activity?.runOnUiThread {
                setDeleteInProgress(false)
                Toast.makeText(context, getString(R.string.profile_delete_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteFirebaseAccount(user: com.google.firebase.auth.FirebaseUser) {
        user.delete().addOnCompleteListener { task ->
            val message = when {
                task.isSuccessful -> getString(R.string.profile_delete_success)
                task.exception is FirebaseAuthRecentLoginRequiredException -> getString(R.string.profile_delete_reauth_required)
                else -> getString(R.string.profile_delete_auth_error)
            }

            authManager.signOut {
                activity?.runOnUiThread {
                    setDeleteInProgress(false)
                    updateUI()
                    loadStatistics()
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    navigateBackToStartPage()
                }
            }
        }
    }

    private fun navigateBackToStartPage() {
        val navController = findNavController()
        val poppedToStart = navController.popBackStack(R.id.startPageFragment, false)
        if (!poppedToStart && navController.currentDestination?.id != R.id.startPageFragment) {
            navController.navigate(R.id.startPageFragment)
        }
    }

    private fun setDeleteInProgress(inProgress: Boolean) {
        if (_binding == null) return
        binding.uploadProgress.visibility = if (inProgress) View.VISIBLE else View.GONE
        binding.btnDeleteAccount.isEnabled = !inProgress
        binding.btnSignOut.isEnabled = !inProgress
        binding.btnEditPhoto.isEnabled = !inProgress
        binding.btnRestoreCloud.isEnabled = !inProgress
        binding.btnOverwriteCloud.isEnabled = !inProgress
    }

    private fun setSyncInProgress(inProgress: Boolean) {
        if (_binding == null) return
        binding.uploadProgress.visibility = if (inProgress) View.VISIBLE else View.GONE
        binding.btnRestoreCloud.isEnabled = !inProgress
        binding.btnOverwriteCloud.isEnabled = !inProgress
        binding.btnSignOut.isEnabled = !inProgress
        binding.btnDeleteAccount.isEnabled = !inProgress
        binding.btnEditPhoto.isEnabled = !inProgress
    }

    private fun loadStatistics() {
        val top5Total = scoreTarget(Constants.Top5)
        val uefaTotal = scoreTarget(Constants.UFA)
        val worldTotal = scoreTarget(Constants.World)
        val versusTotal = scoreTarget(Constants.VSRM) + scoreTarget(Constants.VSRB)
        val totalQuestions = top5Total + uefaTotal + worldTotal + versusTotal

        val top5Answered = dbHelper.top5AnsweredScores
        val uefaAnswered = dbHelper.getUFAAnsweredScores()
        val worldAnswered = dbHelper.getWorldAnsweredScores()
        val versusAnswered = dbHelper.getVersusAnsweredScores()
        val totalAnswered = top5Answered + uefaAnswered + worldAnswered + versusAnswered
        val progressPercent = if (totalQuestions > 0) {
            (totalAnswered * 100) / totalQuestions
        } else {
            0
        }

        binding.tvTotalScoreValue.text = getString(R.string.profile_progress_percent, progressPercent)
        binding.tvAnsweredCountValue.text = progressText(totalAnswered, totalQuestions)
        binding.tvTop5Progress.text = progressText(top5Answered, top5Total)
        binding.tvUefaProgress.text = progressText(uefaAnswered, uefaTotal)
        binding.tvWorldProgress.text = progressText(worldAnswered, worldTotal)
        binding.tvVersusProgress.text = progressText(versusAnswered, versusTotal)
    }

    private fun scoreTarget(value: String): Int = value.toIntOrNull() ?: 0

    private fun progressText(answered: Int, total: Int): String = "$answered/$total"

    private fun restoreFromCloudManually() {
        val user = authManager.getCurrentUser() ?: return
        setSyncInProgress(true)

        CloudSyncManager.downloadCloudStats(user) { cloudStats ->
            activity?.runOnUiThread {
                setSyncInProgress(false)
                if (cloudStats == null) {
                    Toast.makeText(context, getString(R.string.profile_no_cloud_progress), Toast.LENGTH_SHORT).show()
                } else {
                    showRestoreDialog(cloudStats)
                }
            }
        }
    }

    private fun performUpload(user: com.google.firebase.auth.FirebaseUser) {
        setSyncInProgress(true)
        CloudSyncManager.uploadLocalStats(requireContext(), user) { success, error ->
            activity?.runOnUiThread {
                setSyncInProgress(false)
                if (success) {
                    markAutoRestorePromptDismissed(user.uid)
                    Toast.makeText(context, getString(R.string.profile_sync_success), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.profile_sync_error, error ?: getString(R.string.profile_sync_generic_error)),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showOverwriteCloudDialog() {
        val user = authManager.getCurrentUser() ?: return
        val context = context ?: return
        val localScore = dbHelper.getTotalScore()
        val localAnswered = dbHelper.top5AnsweredScores +
            dbHelper.getUFAAnsweredScores() +
            dbHelper.getWorldAnsweredScores() +
            dbHelper.getVersusAnsweredScores()
        DialogManager.showActionDialog(
            context = context,
            inflater = layoutInflater,
            title = getString(R.string.profile_overwrite_cloud_title),
            message = getString(R.string.profile_overwrite_cloud_message, localScore, localAnswered),
            primaryText = getString(R.string.profile_sync_overwrite_button),
            secondaryText = getString(R.string.profile_restore_cancel),
            onPrimaryClick = {
                performUpload(user)
            }
        )
    }

    private fun markAutoRestorePromptDismissed(userId: String) {
        val context = context ?: return
        Prefer.setBooleanPreference(
            context,
            Constants.UserStatsRestorePromptDismissedKeyPrefix + userId,
            true
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
