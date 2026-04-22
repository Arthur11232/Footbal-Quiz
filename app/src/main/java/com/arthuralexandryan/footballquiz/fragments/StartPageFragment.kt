package com.arthuralexandryan.footballquiz.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.FQ_Application
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.auth.AuthManager
import com.arthuralexandryan.footballquiz.constants.Constant.INIT_DB
import com.arthuralexandryan.footballquiz.databinding.ActivityStartPageBinding
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.models.Answers
import com.arthuralexandryan.footballquiz.models.FirestoreQuestionService
import com.arthuralexandryan.footballquiz.models.CloudSyncManager
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.models.IsSetQuestions
import com.arthuralexandryan.footballquiz.models.QuestionModel
import com.arthuralexandryan.footballquiz.models.UserStatsDTO
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.DialogManager
import com.arthuralexandryan.footballquiz.utils.Prefer
import com.arthuralexandryan.footballquiz.utils.SystemBarStyleHelper
import java.util.regex.Pattern

class StartPageFragment : Fragment() {

    private var _binding: ActivityStartPageBinding? = null
    private val binding get() = _binding!!


    private lateinit var firestoreService: FirestoreQuestionService
    private lateinit var authManager: AuthManager
    private lateinit var dbHelper: DB_Helper
    private var isNew: Boolean = false
    private var restoreDialogShowing: Boolean = false

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                account.idToken?.let { token ->
                    authManager.signInWithFirebase(token) { success: Boolean, user: com.google.firebase.auth.FirebaseUser? ->
                        if (success) {
                            updateUI(user)
                            user?.let { maybeSyncCloudProgress(it) }
                        } else {
                            android.widget.Toast.makeText(requireContext(), "Firebase Auth Failed", android.widget.Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("StartPageFragment", "Sign-in failed", e)
                updateUI(null)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = ActivityStartPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(getString(R.string.title_start_page))

        binding.tvGame.setOnClickListener { handleGameClick() }
        binding.tvContinue.setOnClickListener{ handleContinueClick() }
        binding.tvLanguage.setOnClickListener{ handleLanguageClick() }
        binding.signInPlay.setOnClickListener{ handleSignInClick() }
        binding.myAccount.setOnClickListener{ handleProfileClick() }


        initView()
        setAgreements()
        val currentUser = authManager.getCurrentUser()
        updateUI(currentUser)
    }

    private fun updateUI(user: com.google.firebase.auth.FirebaseUser?) {
        if (_binding == null) return
        if (user != null) {
            binding.signInPlay.visibility = View.GONE
            binding.myAccount.visibility = View.VISIBLE
            binding.myAccountEmail.text = user.email ?: getString(R.string.profile_not_signed_in)
        } else {
            binding.signInPlay.visibility = View.VISIBLE
            binding.myAccount.visibility = View.GONE
        }
    }

    private fun setToolbar(title: String) {
        (activity as? AppCompatActivity)?.let {
            it.setSupportActionBar(binding.toolbar)
            it.supportActionBar?.title = title
        }
    }

    override fun onStart() {
        super.onStart()
        refreshContinueButton()
        authManager.getCurrentUser()?.let { maybeSyncCloudProgress(it) }
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

    private fun refreshContinueButton() {
        if (Prefer.getBooleanPreference(requireContext(), "isFirstPlay", true)) {
            binding.tvContinue.isEnabled = false
            binding.tvContinue.alpha = 0.33f
            isNew = true
        } else {
            isNew = false
            binding.tvContinue.alpha = 1f
            binding.tvContinue.isEnabled = true
        }
    }

    private fun initView() {
        dbHelper = DB_Helper()

        firestoreService = FirestoreQuestionService.getInstance()
        authManager = AuthManager(requireContext())
    }

    private fun showLoading() {
        binding.loadingLayout.visibility = View.VISIBLE
        binding.tvGame.isEnabled = false
        binding.tvContinue.isEnabled = false
        binding.tvLanguage.isEnabled = false
        binding.signInPlay.isEnabled = false
        binding.myAccount.isEnabled = false
    }

    private fun hideLoading() {
        binding.loadingLayout.visibility = View.GONE
        binding.tvGame.isEnabled = true
        binding.tvLanguage.isEnabled = true
        binding.signInPlay.isEnabled = true
        binding.myAccount.isEnabled = true
        refreshContinueButton()
    }

    private fun fetchQuestionsFromFirestore(onComplete: () -> Unit) {
        val selectedLocale = Prefer.getStringPreference(requireContext(), Constants.Localization, "ru") ?: "ru"
        Log.d("FQ_Log", "fetchQuestionsFromFirestore: locale=$selectedLocale, isNew=$isNew")
        showLoading()

        firestoreService.getQuestions(selectedLocale) { success, questionsList ->
            if (success && questionsList != null) {
                Log.d("FQ_Log", "fetchQuestionsFromFirestore: loaded ${questionsList.size} questions for locale=$selectedLocale")
                val mappedQuestions = questionsList.map { q ->
                    QuestionModel().apply {
                        question = q.question ?: ""
                        place = q.type ?: ""
                        answered = q.isAnswered
                        answers = Answers().apply {
                            A = q.answer_A ?: ""
                            B = q.answer_B ?: ""
                            C = q.answer_C ?: ""
                            D = q.answer_D ?: ""
                            right = q.right_answer ?: ""
                        }
                    }
                }

                requireActivity().runOnUiThread {
                    DB_Helper().setQuestionsToDB(mappedQuestions, isNew, IsSetQuestions {
                        Prefer.setBooleanPreference(requireContext(), INIT_DB, true)
                        // Track which language is actually in the database now
                        Prefer.setStringPreference(requireContext(), "db_lang", selectedLocale)
                        Log.d("FQ_Log", "fetchQuestionsFromFirestore: questions saved locally, locale=$selectedLocale")
                        hideLoading()
                        onComplete()
                    })
                }
            } else {
                Log.e("FQ_Log", "fetchQuestionsFromFirestore: failed to load questions for locale=$selectedLocale")
                requireActivity().runOnUiThread {
                    hideLoading()
                    android.widget.Toast.makeText(requireContext(), "Internet connection required to download questions.", android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun ensureLocalizationSync(onReady: () -> Unit) {
        val selectedLocale = Prefer.getStringPreference(requireContext(), Constants.Localization, "en") ?: "en"
        val questionsLocale = Prefer.getStringPreference(requireContext(), "db_lang", "")
        val isInitialized = Prefer.getBooleanPreference(requireContext(), INIT_DB, false)
        Log.d(
            "FQ_Log",
            "ensureLocalizationSync: selectedLocale=$selectedLocale, questionsLocale=$questionsLocale, isInitialized=$isInitialized"
        )

        if (isInitialized && selectedLocale == questionsLocale) {
            Log.d("FQ_Log", "ensureLocalizationSync: local questions are up to date")
            onReady()
        } else {
            Log.d("FQ_Log", "ensureLocalizationSync: refreshing questions from Firestore")
            fetchQuestionsFromFirestore {
                onReady()
            }
        }
    }

    private fun onCheck() {
        val realm = io.realm.Realm.getDefaultInstance()
        realm.executeTransaction { r -> DB_Helper().setDefaultAllScores(r) }
        realm.close()
    }

    private fun handleGameClick() {
        Log.d("FQ_Log", "handleGameClick: user=${authManager.getCurrentUser()?.uid ?: "guest"}, isNew=$isNew")
        ensureLocalizationSync {
            if (isNew) {
                Log.d("FQ_Log", "handleGameClick: starting a new game immediately")
                newGame(requireContext(), true)
            } else {
                Log.d("FQ_Log", "handleGameClick: showing new game dialog")
                DialogManager.showNewGameDialog(
                    context = requireContext(),
                    inflater = layoutInflater
                ) {
                    newGame(requireContext(), true)
                }
            }
        }
    }

    private fun handleContinueClick() {
        ensureLocalizationSync {
            findNavController().navigate(R.id.action_start_to_choose)
        }
    }

    private fun handleLanguageClick() {
        openLanguageDialog()
    }

    private fun handleSignInClick() {
        signInLauncher.launch(authManager.getSignInIntent())
    }

    private fun handleProfileClick() {
        findNavController().navigate(R.id.action_start_to_profile)
    }

    private fun maybeSyncCloudProgress(user: com.google.firebase.auth.FirebaseUser) {
        if (_binding == null) return
        Log.d("FQ_Log", "maybeSyncCloudProgress: checking cloud sync for user=${user.uid}")
        CloudSyncManager.resolveSyncDecision(requireContext(), dbHelper, user) { decision ->
            activity?.runOnUiThread {
                when (decision) {
                    is CloudSyncManager.SyncDecision.None -> {
                        Log.d("FQ_Log", "maybeSyncCloudProgress: no sync action needed for user=${user.uid}")
                    }
                    is CloudSyncManager.SyncDecision.RestoreCloud -> {
                        if (isAutoRestorePromptDismissed(user.uid)) {
                            Log.d("FQ_Log", "maybeSyncCloudProgress: restore prompt already dismissed for user=${user.uid}")
                        } else {
                            Log.d("FQ_Log", "maybeSyncCloudProgress: restore requested from cloud for user=${user.uid}")
                            showRestoreProgressDialog(user.uid, decision.cloudStats)
                        }
                    }
                    is CloudSyncManager.SyncDecision.UploadLocal -> {
                        Log.d("FQ_Log", "maybeSyncCloudProgress: uploading local progress for user=${user.uid}")
                        CloudSyncManager.uploadLocalStats(requireContext(), decision.user) { success, _ ->
                            if (success && isAdded) {
                                activity?.runOnUiThread {
                                    android.widget.Toast.makeText(
                                        requireContext(),
                                        getString(R.string.profile_cloud_backup_created),
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            Log.d("FQ_Log", "maybeSyncCloudProgress: upload result success=$success for user=${user.uid}")
                        }
                    }
                }
            }
        }
    }

    private fun isAutoRestorePromptDismissed(userId: String): Boolean {
        return Prefer.getBooleanPreference(
            requireContext(),
            Constants.UserStatsRestorePromptDismissedKeyPrefix + userId,
            false
        )
    }

    private fun dismissAutoRestorePrompt(userId: String) {
        val context = context ?: return
        Prefer.setBooleanPreference(
            context,
            Constants.UserStatsRestorePromptDismissedKeyPrefix + userId,
            true
        )
    }

    private fun showRestoreProgressDialog(userId: String, cloudStats: UserStatsDTO) {
        if (!isAdded) return
        if (restoreDialogShowing) return
        restoreDialogShowing = true

        var handledByButton = false
        DialogManager.showActionDialog(
            context = requireContext(),
            inflater = layoutInflater,
            title = getString(R.string.profile_restore_title),
            message = getString(R.string.profile_restore_message, cloudStats.gameState.total),
            primaryText = getString(R.string.profile_restore_confirm),
            secondaryText = getString(R.string.profile_restore_cancel),
            cancelOutside = true,
            onDismiss = {
                if (!handledByButton) {
                    dismissAutoRestorePrompt(userId)
                }
                restoreDialogShowing = false
            },
            onPrimaryClick = {
                handledByButton = true
                CloudSyncManager.restoreCloudStats(requireContext(), dbHelper, userId, cloudStats)
                refreshContinueButton()
                android.widget.Toast.makeText(
                    requireContext(),
                    getString(R.string.profile_progress_restored),
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            },
            onSecondaryClick = {
                handledByButton = true
                dismissAutoRestorePrompt(userId)
            }
        )
    }

    private fun newGame(context: Context, isFromDB: Boolean) {
        Prefer.setBooleanPreference(context, "isFirstPlay", false)
        if (isFromDB) onCheck()
        findNavController().navigate(R.id.action_start_to_choose)
    }

    private fun openLanguageDialog() {
        DialogManager.showLanguageDialog(
            context = requireContext(),
            inflater = layoutInflater,
            checkedLanguageId = Prefer.getIntPreference(requireContext(), Constants.CheckedLanguage, R.id.lng_russian),
            onLanguageSelected = { radioGroup, checkedId ->
                val locale = when (checkedId) {
                    R.id.lng_armenian -> {
                        Prefer.setIntPreference(requireContext(), Constants.CheckedLanguage, R.id.lng_armenian)
                        "hy"
                    }

                    R.id.lng_russian -> {
                        Prefer.setIntPreference(requireContext(), Constants.CheckedLanguage, R.id.lng_russian)
                        "ru"
                    }

                    else -> {
                        Prefer.setIntPreference(requireContext(), Constants.CheckedLanguage, R.id.lng_english)
                        "en"
                    }
                }
                Prefer.setStringPreference(requireContext(), Constants.Localization, locale)
                for (j in 0 until radioGroup.childCount) radioGroup.getChildAt(j).isClickable = false
            },
            onSave = {
                requireActivity().recreate()
            }
        )
    }

    private fun setAgreements() {
        val agreementSpannable = SpannableString(getString(R.string.agree_to))

        val privacyPattern = Pattern.compile(getString(R.string.title_privacy_policy))
        val privacyMatcher = privacyPattern.matcher(agreementSpannable)
        if (privacyMatcher.find()) {
            val start = privacyMatcher.start()
            val end = privacyMatcher.end()
            agreementSpannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.fq_colorBackground)),
                    start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            agreementSpannable.setSpan(object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }

                override fun onClick(widget: View) {
                    openExternalLink(getString(R.string.privacy_policy_url))
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val termsPattern = Pattern.compile(getString(R.string.title_terms_and_conditions))
        val termsMatcher = termsPattern.matcher(agreementSpannable)
        if (termsMatcher.find()) {
            val start = termsMatcher.start()
            val end = termsMatcher.end()
            agreementSpannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.fq_colorBackground)),
                    start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            agreementSpannable.setSpan(object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }

                override fun onClick(widget: View) {
                    openExternalLink(getString(R.string.terms_and_conditions_url))
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.agreement.text = agreementSpannable
        binding.agreement.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun openExternalLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
