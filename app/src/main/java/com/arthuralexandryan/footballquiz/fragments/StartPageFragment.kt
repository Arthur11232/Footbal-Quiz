package com.arthuralexandryan.footballquiz.fragments

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
import com.arthuralexandryan.footballquiz.models.IsSetQuestions
import com.arthuralexandryan.footballquiz.models.FirestoreQuestionService
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.models.QuestionModel
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer
import java.util.regex.Pattern

class StartPageFragment : Fragment() {

    private var _binding: ActivityStartPageBinding? = null
    private val binding get() = _binding!!


    private lateinit var firestoreService: FirestoreQuestionService
    private lateinit var authManager: AuthManager
    private lateinit var dbHelper: DB_Helper
    private var isNew: Boolean = false

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                account.idToken?.let { token ->
                    authManager.signInWithFirebase(token) { success: Boolean, user: com.google.firebase.auth.FirebaseUser? ->
                        if (success) {
                            updateUI(user)
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
        updateUI(authManager.getCurrentUser())
    }

    private fun updateUI(user: com.google.firebase.auth.FirebaseUser?) {
        if (_binding == null) return
        if (user != null) {
            binding.signInPlay.visibility = View.GONE
            binding.myAccount.visibility = View.VISIBLE
            binding.myAccount.text = user.email
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
    }

    private fun hideLoading() {
        binding.loadingLayout.visibility = View.GONE
        binding.tvGame.isEnabled = true
        binding.tvLanguage.isEnabled = true
        binding.signInPlay.isEnabled = true
        refreshContinueButton()
    }

    private fun fetchQuestionsFromFirestore(onComplete: () -> Unit) {
        val selectedLocale = Prefer.getStringPreference(requireContext(), Constants.Localization, "ru") ?: "ru"
        
        showLoading()

        firestoreService.getQuestions(selectedLocale) { success, questionsList ->
            if (success && questionsList != null) {
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
                        
                        hideLoading()
                        onComplete()
                    })
                }
            } else {
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

        if (isInitialized && selectedLocale == questionsLocale) {
            onReady()
        } else {
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
        ensureLocalizationSync {
            if (isNew) {
                newGame(requireContext(), true)
            } else {
                getNewGameDialog(requireContext()).show()
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

    private fun newGame(context: Context, isFromDB: Boolean) {
        Prefer.setBooleanPreference(context, "isFirstPlay", false)
        if (isFromDB) onCheck()
        findNavController().navigate(R.id.action_start_to_choose)
    }

    private fun getNewGameDialog(context: Context): android.app.Dialog {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_game, null)
        val dialog = android.app.Dialog(context, R.style.FQ_CustomDialog)
        dialog.setContentView(dialogView)
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                    (resources.displayMetrics.widthPixels * 0.9).toInt(),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_yes).setOnClickListener {
            dialog.dismiss()
            newGame(context, true)
        }
        dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_no).setOnClickListener {
            dialog.dismiss()
        }
        return dialog
    }

    private fun openLanguageDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_language, null)
        val dialog = android.app.Dialog(requireContext(), R.style.FQ_CustomDialog)
        dialog.setContentView(dialogView)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                    (resources.displayMetrics.widthPixels * 0.9).toInt(),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        val group = dialogView.findViewById<RadioGroup>(R.id.app_languages)
        group.check(Prefer.getIntPreference(requireContext(), Constants.CheckedLanguage, R.id.lng_russian))
        group.setOnCheckedChangeListener { radioGroup, i ->
            val locale = when (i) {
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
        }

        dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.save_language).setOnClickListener {
            dialog.dismiss()
            requireActivity().recreate()
        }

        dialog.show()
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
                    findNavController().navigate(R.id.action_start_to_privacy)
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
                    findNavController().navigate(R.id.action_start_to_terms)
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.agreement.text = agreementSpannable
        binding.agreement.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
