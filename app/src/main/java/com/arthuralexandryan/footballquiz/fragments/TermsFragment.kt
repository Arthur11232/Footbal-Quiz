package com.arthuralexandryan.footballquiz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.databinding.ActivityTermsBinding

class TermsFragment : Fragment() {

    private var _binding: ActivityTermsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityTermsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setToolbar(getString(R.string.title_terms_and_conditions))
        binding.privacyJustifiedText.setText(getString(R.string.fq_terms_conditions))
        
        binding.toolbar.setNavigationIcon(R.drawable.baseline_keyboard_backspace_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setToolbar(title: String) {
        (activity as? AppCompatActivity)?.let {
            it.setSupportActionBar(binding.toolbar)
            it.supportActionBar?.title = title
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
