package com.arthuralexandryan.footballquiz.fragments

import android.annotation.SuppressLint
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
import com.arthuralexandryan.footballquiz.databinding.ActivityStartPageBinding
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.models.GetQuestions
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer
import java.util.regex.Pattern

class StartPageFragment : Fragment(), View.OnClickListener {

    private var _binding: ActivityStartPageBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var questions: GetQuestions
    private lateinit var dbHelper: DB_Helper
    private var isNew: Boolean = false

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

        initView()
        setAgreements()
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
        questions = GetQuestions(requireContext())
    }

    fun onCheck(isNew: Boolean) {
        questions.getAllQuestions()
        FQ_Application.getInstance().setDB(dbHelper, questions, isNew)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvGame -> {
                if (isNew) {
                    newGame(requireContext())
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
        }
    }

    private fun newGame(context: Context) {
        Prefer.setBooleanPreference(context, "isFirstPlay", false)
        onCheck(true)
        findNavController().navigate(R.id.action_start_to_choose)
    }

    private fun getNewGameDialog(context: Context): AlertDialog {
        return AlertDialog.Builder(context).setTitle(R.string.new_game)
            .setMessage(R.string.text_for_new_game)
            .setPositiveButton(R.string.new_yes) { _, _ ->
                newGame(context)
            }
            .setNegativeButton(R.string.new_no) { dialog, _ -> dialog.dismiss() }
            .create()
    }

    private fun openLanguageDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.dialog_languages)
        builder.setTitle(R.string.languages)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        val group = dialog.findViewById<RadioGroup>(R.id.app_languages)
        if (group != null) {
            group.check(Prefer.getIntPreference(requireContext(), Constants.CheckedLanguage, R.id.lng_russian))
            group.setOnCheckedChangeListener { radioGroup, i ->
                var locale = "ru"
                if (i == R.id.lng_armenian) {
                    locale = getString(R.string.local_armenian)
                    Prefer.setIntPreference(requireContext(), Constants.CheckedLanguage, R.id.lng_armenian)
                } else if (i == R.id.lng_russian) {
                    locale = getString(R.string.local_russian)
                    Prefer.setIntPreference(requireContext(), Constants.CheckedLanguage, R.id.lng_russian)
                }
                Prefer.setStringPreference(requireContext(), Constants.Localization, locale)

                for (j in 0 until radioGroup.childCount) {
                    radioGroup.getChildAt(j).isClickable = false
                }

                object : CountDownTimer(1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        dialog.dismiss()
                        requireActivity().recreate()
                    }
                }.start()
            }
        }
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
