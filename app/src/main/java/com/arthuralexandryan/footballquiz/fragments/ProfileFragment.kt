package com.arthuralexandryan.footballquiz.fragments

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.auth.AuthManager
import com.arthuralexandryan.footballquiz.databinding.FragmentProfileBinding
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.models.CloudSyncManager
import com.arthuralexandryan.footballquiz.utils.Prefer
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var authManager: AuthManager
    private lateinit var dbHelper: DB_Helper

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { authResult ->
                        if (authResult.isSuccessful) {
                            updateUI()
                        } else {
                            Toast.makeText(context, "Auth Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

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

        binding.btnSignIn.setOnClickListener {
            signInLauncher.launch(authManager.getSignInIntent())
        }

        binding.btnSignOut.setOnClickListener {
            authManager.signOut { 
                isCloudStatsChecked = false
                updateUI() 
            }
        }

        binding.btnEditPhoto.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnSync.setOnClickListener {
            syncStats()
        }

        binding.onBack.setOnClickListener { findNavController().navigateUp() }
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
            binding.btnSignIn.visibility = View.GONE
            binding.btnSignOut.visibility = View.VISIBLE
            binding.btnEditPhoto.visibility = View.VISIBLE
            binding.btnSync.visibility = View.GONE
            
            checkCloudStats()

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
            binding.btnSignIn.visibility = View.VISIBLE
            binding.btnSignOut.visibility = View.GONE
            binding.btnEditPhoto.visibility = View.GONE
            binding.btnSync.visibility = View.GONE
            
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

    private fun checkCloudStats() {
        if (isCloudStatsChecked) return
        val user = authManager.getCurrentUser() ?: return

        CloudSyncManager.resolveSyncDecision(requireContext(), dbHelper, user) { decision ->
            activity?.runOnUiThread {
                if (decision is CloudSyncManager.SyncDecision.RestoreCloud) {
                    showRestoreDialog(decision.cloudStats)
                }
                isCloudStatsChecked = true
            }
        }
    }

    private fun showRestoreDialog(cloudStats: com.arthuralexandryan.footballquiz.models.UserStatsDTO) {
        val context = context ?: return
        val dialogView = layoutInflater.inflate(R.layout.dialog_cloud_sync, null)
        val dialog = android.app.Dialog(context, R.style.FQ_CustomDialog)
        dialog.setContentView(dialogView)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        dialogView.findViewById<android.widget.TextView>(R.id.dialogTitle).text = getString(R.string.profile_restore_title)
        dialogView.findViewById<android.widget.TextView>(R.id.dialogMessage).text =
            getString(R.string.profile_restore_message, cloudStats.gameState.total)

        dialogView.findViewById<AppCompatButton>(R.id.btnPrimary).apply {
            text = getString(R.string.profile_restore_confirm)
            setOnClickListener {
                dialog.dismiss()
                val userId = authManager.getCurrentUser()?.uid ?: return@setOnClickListener
                CloudSyncManager.restoreCloudStats(requireContext(), dbHelper, userId, cloudStats)
                updateUI()
                loadStatistics()
                Toast.makeText(context, getString(R.string.profile_progress_restored), Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<AppCompatButton>(R.id.btnSecondary).apply {
            text = getString(R.string.profile_restore_cancel)
            setOnClickListener { dialog.dismiss() }
        }

        dialog.show()
    }

    private fun loadStatistics() {
        val totalScore = dbHelper.getTotalScore()
        binding.tvTotalScoreValue.text = totalScore.toString()

        val totalAnswered = dbHelper.top5AnsweredScores +
                dbHelper.getUFAAnsweredScores() +
                dbHelper.getWorldAnsweredScores() +
                dbHelper.getVersusAnsweredScores()

        binding.tvAnsweredCountValue.text = totalAnswered.toString()
    }

    private fun syncStats() {
        val user = authManager.getCurrentUser() ?: return

        // Animation
        val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 1000
            repeatCount = Animation.INFINITE
        }
        binding.btnSync.startAnimation(rotate)
        binding.btnSync.isEnabled = false

        CloudSyncManager.resolveSyncDecision(requireContext(), dbHelper, user) { decision ->
            activity?.runOnUiThread {
                binding.btnSync.clearAnimation()
                binding.btnSync.isEnabled = true

                when (decision) {
                    is CloudSyncManager.SyncDecision.None -> {
                        Toast.makeText(context, getString(R.string.profile_sync_up_to_date), Toast.LENGTH_SHORT).show()
                    }
                    is CloudSyncManager.SyncDecision.RestoreCloud -> {
                        val localScore = dbHelper.getTotalScore()
                        showSyncConflictDialog(decision.cloudStats, localScore)
                    }
                    is CloudSyncManager.SyncDecision.UploadLocal -> {
                        performUpload(decision.user)
                    }
                }
            }
        }
    }

    private fun performUpload(user: com.google.firebase.auth.FirebaseUser) {
        CloudSyncManager.uploadLocalStats(requireContext(), user) { success, error ->
            activity?.runOnUiThread {
                if (success) {
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

    private fun showSyncConflictDialog(cloudStats: com.arthuralexandryan.footballquiz.models.UserStatsDTO, localScore: Int) {
        val context = context ?: return
        val dialogView = layoutInflater.inflate(R.layout.dialog_cloud_sync, null)
        val dialog = android.app.Dialog(context, R.style.FQ_CustomDialog)
        dialog.setContentView(dialogView)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        dialogView.findViewById<android.widget.TextView>(R.id.dialogTitle).text = getString(R.string.profile_sync_conflict_title)
        dialogView.findViewById<android.widget.TextView>(R.id.dialogMessage).text =
            getString(R.string.profile_sync_conflict_message, cloudStats.gameState.total, localScore)

        dialogView.findViewById<AppCompatButton>(R.id.btnPrimary).apply {
            text = getString(R.string.profile_sync_restore_button)
            setOnClickListener {
                dialog.dismiss()
                val userId = authManager.getCurrentUser()?.uid ?: return@setOnClickListener
                CloudSyncManager.restoreCloudStats(requireContext(), dbHelper, userId, cloudStats)
                updateUI()
                loadStatistics()
                Toast.makeText(context, getString(R.string.profile_progress_restored), Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<AppCompatButton>(R.id.btnSecondary).apply {
            text = getString(R.string.profile_sync_overwrite_button)
            setOnClickListener {
                dialog.dismiss()
                val user = authManager.getCurrentUser()
                if (user != null) performUpload(user)
            }
        }

        dialogView.findViewById<AppCompatButton>(R.id.btnTertiary).apply {
            visibility = View.VISIBLE
            text = getString(R.string.profile_restore_cancel)
            setOnClickListener { dialog.dismiss() }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private var isCloudStatsChecked = false
    }
}
