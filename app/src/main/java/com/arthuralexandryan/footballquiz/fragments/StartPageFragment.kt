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
import com.arthuralexandryan.footballquiz.models.IsSetQuestions
import com.arthuralexandryan.footballquiz.models.FirestoreQuestionService
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer
import java.util.regex.Pattern

class StartPageFragment : Fragment(), View.OnClickListener {

    private var _binding: ActivityStartPageBinding? = null
    private val binding get() = _binding!!
    

    private lateinit var firestoreService: FirestoreQuestionService
    private lateinit var authManager: AuthManager
    private lateinit var dbHelper: DB_Helper
    private var isNew: Boolean = false
    
    private val signInLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
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

        binding.tvGame.setOnClickListener(this)
        binding.tvContinue.setOnClickListener(this)
        binding.tvLanguage.setOnClickListener(this)
        binding.signInPlay.setOnClickListener(this)
        binding.myAccount.setOnClickListener(this)


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

    fun fetchQuestionsFromFirestore() {
        binding.tvGame.isEnabled = false
        binding.tvGame.text = "Loading..."
        
        firestoreService.getQuestions(
            Prefer.getStringPreference(requireContext(), Constants.Localization, "en") ?: "en"
        ) { success, questionsList ->
            if (success && questionsList != null) {
                val mappedQuestions = questionsList.map { q ->
                    com.arthuralexandryan.footballquiz.models.QuestionModel().apply {
                        question = q.question ?: ""
                        place = q.type ?: ""
                        answered = q.isAnswered
                        answers = com.arthuralexandryan.footballquiz.models.Answers().apply {
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
                        binding.tvGame.isEnabled = true
                        binding.tvGame.setText(R.string.new_game)
                        newGame(requireContext(), false)
                    })
                }
            } else {
                requireActivity().runOnUiThread {
                    binding.tvGame.isEnabled = true
                    binding.tvGame.setText(R.string.new_game)
                    android.widget.Toast.makeText(requireContext(), "Internet connection required for first-time setup.", android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun onCheck() {
        val realm = io.realm.Realm.getDefaultInstance()
        realm.executeTransaction { r -> DB_Helper().setDefaultAllScores(r) }
        realm.close()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvGame -> {
                if (isNew) {
                    if (Prefer.getBooleanPreference(requireContext(), INIT_DB, false)) {
                        newGame(requireContext(), true)
                    } else {
                        fetchQuestionsFromFirestore()
                    }
                } else {
                    getNewGameDialog(requireContext()).show()
                }
            }
            R.id.tvContinue -> {
                findNavController().navigate(R.id.action_start_to_choose)
            }
            R.id.tvLanguage -> {
                openLanguageDialog()
            }
            R.id.sign_in_play -> {
                signInLauncher.launch(authManager.getSignInIntent())
            }
            R.id.my_account -> {
                findNavController().navigate(R.id.action_start_to_profile)
            }
        }
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
            var locale = "ru"
            when (i) {
                R.id.lng_armenian -> {
                    locale = getString(R.string.local_armenian)
                    Prefer.setIntPreference(requireContext(), Constants.CheckedLanguage, R.id.lng_armenian)
                }
                R.id.lng_russian -> {
                    locale = getString(R.string.local_russian)
                    Prefer.setIntPreference(requireContext(), Constants.CheckedLanguage, R.id.lng_russian)
                }
                else -> {
                    locale = "en"
                    Prefer.setIntPreference(requireContext(), Constants.CheckedLanguage, R.id.lng_english)
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
                override fun updateDrawState(ds: TextPaint) { ds.isUnderlineText = false }
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
                override fun updateDrawState(ds: TextPaint) { ds.isUnderlineText = false }
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
