package com.arthuralexandryan.footballquiz.fragments

import android.Manifest
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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.auth.AuthManager
import com.arthuralexandryan.footballquiz.data.repository.SettingsRepository
import com.arthuralexandryan.footballquiz.data.source.local.GameStateLocalDataSource
import com.arthuralexandryan.footballquiz.databinding.ActivityStartPageBinding
import com.arthuralexandryan.footballquiz.presentation.start.StartPageEffect
import com.arthuralexandryan.footballquiz.presentation.start.StartPageViewModel
import com.arthuralexandryan.footballquiz.presentation.sync.CloudRestorePromptUiModel
import com.arthuralexandryan.footballquiz.utils.DialogManager
import com.arthuralexandryan.footballquiz.utils.SystemBarStyleHelper
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Pattern

class StartPageFragment : Fragment() {

    private var _binding: ActivityStartPageBinding? = null
    private val binding get() = _binding!!


    private val authManager: AuthManager by inject()
    private val gameStateLocalDataSource: GameStateLocalDataSource by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val startPageViewModel: StartPageViewModel by viewModel()
    private var restoreDialogShowing: Boolean = false

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (_binding == null) return@registerForActivityResult
            startPageViewModel.onNotificationPermissionResult(granted)
        }

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (_binding == null) return@registerForActivityResult
        showLoading(R.string.signing_in)
        val signInData = result.data
        if (signInData == null) {
            Log.e("FQ_Auth", "Google sign-in returned null intent data. resultCode=${result.resultCode}")
            startPageViewModel.onGoogleSignInIntentDataMissing()
            return@registerForActivityResult
        }

        val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(signInData)
        try {
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            Log.d(
                "FQ_Auth",
                "Google sign-in account received. resultCode=${result.resultCode}, email=${account.email}, hasIdToken=${!account.idToken.isNullOrEmpty()}"
            )
            account.idToken?.let { token ->
                startPageViewModel.onGoogleIdTokenReceived(token)
            } ?: run {
                Log.e("FQ_Auth", "Google sign-in returned null idToken. resultCode=${result.resultCode}")
                startPageViewModel.onGoogleSignInIdTokenMissing()
            }
        } catch (e: com.google.android.gms.common.api.ApiException) {
            Log.e(
                "FQ_Auth",
                "Google sign-in ApiException. resultCode=${result.resultCode}, statusCode=${e.statusCode}, message=${e.localizedMessage}",
                e
            )
            startPageViewModel.onGoogleSignInFailure()
        } catch (e: Exception) {
            Log.e("FQ_Auth", "Unexpected sign-in failure: ${e.localizedMessage}", e)
            startPageViewModel.onGoogleSignInFailure()
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
        initListeners()
        observeUiState()
        observeEffects()
        setAgreements()
        startPageViewModel.refreshState()
    }

    private fun initListeners() = binding.run {
        tvGame.setOnClickListener { startPageViewModel.onGameClicked() }
        tvContinue.setOnClickListener { startPageViewModel.onContinueClicked() }
        tvLanguage.setOnClickListener { startPageViewModel.onLanguageClicked() }
        signInPlay.setOnClickListener { handleSignInClick() }
        myAccount.setOnClickListener { startPageViewModel.onProfileClicked() }
    }

    private fun setToolbar(title: String) {
        (activity as? AppCompatActivity)?.let {
            it.setSupportActionBar(binding.toolbar)
            it.supportActionBar?.title = title
        }
    }

    override fun onStart() {
        super.onStart()
        startPageViewModel.onScreenStarted()
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

    private fun showLoading(messageResId: Int = R.string.loading) {
        startPageViewModel.showLoading(messageResId)
    }

    private fun onCheck() {
        gameStateLocalDataSource.resetScoresToDefault()
    }

    private fun handleSignInClick() {
        showLoading(R.string.signing_in)
        signInLauncher.launch(authManager.getSignInIntent())
    }

    private fun showRestoreProgressDialog(prompt: CloudRestorePromptUiModel) {
        if (!isAdded) return
        if (restoreDialogShowing) return
        if (settingsRepository.isAutoRestorePromptDismissed(prompt.userId)) return
        restoreDialogShowing = true

        DialogManager.showActionDialog(
            context = requireContext(),
            inflater = layoutInflater,
            title = getString(R.string.profile_restore_title),
            message = getString(R.string.profile_restore_conflict_message, prompt.totalScore),
            primaryText = getString(R.string.profile_restore_confirm),
            secondaryText = getString(R.string.profile_restore_cancel),
            cancelOutside = false,
            cancelable = false,
            onDismiss = {
                restoreDialogShowing = false
            },
            onPrimaryClick = {
                startPageViewModel.onCloudRestoreConfirmed(prompt.userId)
            },
            onSecondaryClick = {
                startPageViewModel.onKeepLocalProgressConfirmed(prompt.userId)
            }
        )
    }

    private fun newGame(isFromDB: Boolean) {
        settingsRepository.setFirstPlay(false)
        if (isFromDB) onCheck()
        findNavController().navigate(R.id.action_start_to_choose)
    }

    private fun openLanguageDialog() {
        DialogManager.showLanguageDialog(
            context = requireContext(),
            inflater = layoutInflater,
            checkedLanguageId = settingsRepository.getCheckedLanguage(R.id.lng_english),
            onLanguageSelected = { radioGroup, checkedId ->
                val locale = when (checkedId) {
                    R.id.lng_armenian -> {
                        settingsRepository.setCheckedLanguage(R.id.lng_armenian)
                        "hy"
                    }

                    R.id.lng_russian -> {
                        settingsRepository.setCheckedLanguage(R.id.lng_russian)
                        "ru"
                    }

                    R.id.lng_spanish -> {
                        settingsRepository.setCheckedLanguage(R.id.lng_spanish)
                        "es"
                    }

                    R.id.lng_german -> {
                        settingsRepository.setCheckedLanguage(R.id.lng_german)
                        "de"
                    }

                    R.id.lng_italian -> {
                        settingsRepository.setCheckedLanguage(R.id.lng_italian)
                        "it"
                    }

                    else -> {
                        settingsRepository.setCheckedLanguage(R.id.lng_english)
                        "en"
                    }
                }
                settingsRepository.setSelectedLocale(locale)
            },
            onSave = {
                requireActivity().recreate()
            }
        )
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                startPageViewModel.uiState.collect { state ->
                    if (_binding == null) return@collect

                    binding.loadingLayout.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.tvLoading.setText(state.loadingMessageResId)

                    binding.tvGame.isEnabled = !state.isLoading
                    binding.tvLanguage.isEnabled = !state.isLoading
                    binding.signInPlay.isEnabled = !state.isLoading
                    binding.myAccount.isEnabled = !state.isLoading
                    binding.tvContinue.isEnabled = !state.isLoading && state.isContinueEnabled
                    binding.tvContinue.alpha = if (state.isContinueEnabled) 1f else 0.33f

                    if (state.isSignedIn) {
                        binding.signInPlay.visibility = View.GONE
                        binding.myAccount.visibility = View.VISIBLE
                        binding.myAccountEmail.text = state.accountEmail ?: getString(R.string.profile_not_signed_in)
                    } else {
                        binding.signInPlay.visibility = View.VISIBLE
                        binding.myAccount.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                startPageViewModel.effects.collect { effect ->
                    when (effect) {
                        is StartPageEffect.NavigateToChoose -> {
                            if (effect.startNewGame) {
                                Log.d("FQ_Log", "StartPageEffect: NavigateToChoose(startNewGame=true)")
                                newGame(true)
                            } else {
                                Log.d("FQ_Log", "StartPageEffect: NavigateToChoose(startNewGame=false)")
                                findNavController().navigate(R.id.action_start_to_choose)
                            }
                        }
                        StartPageEffect.ShowNewGameDialog -> {
                            Log.d("FQ_Log", "StartPageEffect: ShowNewGameDialog")
                            DialogManager.showNewGameDialog(
                                context = requireContext(),
                                inflater = layoutInflater
                            ) {
                                newGame(true)
                            }
                        }
                        StartPageEffect.OpenLanguageDialog -> {
                            openLanguageDialog()
                        }
                        StartPageEffect.OpenProfile -> {
                            findNavController().navigate(R.id.action_start_to_profile)
                        }
                        is StartPageEffect.PromptNotificationPermission -> {
                            showNotificationPermissionDialog()
                        }
                        is StartPageEffect.PromptCloudRestore -> {
                            showRestoreProgressDialog(effect.prompt)
                        }
                        is StartPageEffect.ShowToastRes -> {
                            android.widget.Toast.makeText(
                                requireContext(),
                                getString(effect.messageResId),
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun showNotificationPermissionDialog() {
        if (!isAdded) return
        DialogManager.showActionDialog(
            context = requireContext(),
            inflater = layoutInflater,
            title = getString(R.string.notifications_permission_title),
            message = getString(R.string.notifications_permission_message),
            primaryText = getString(R.string.notifications_permission_allow),
            secondaryText = getString(R.string.notifications_permission_not_now),
            onPrimaryClick = {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
