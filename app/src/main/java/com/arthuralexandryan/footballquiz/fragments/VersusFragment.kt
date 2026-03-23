package com.arthuralexandryan.footballquiz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.databinding.ActivityVersusBinding
import com.arthuralexandryan.footballquiz.models.TarberakArrays
import com.arthuralexandryan.footballquiz.versus.vsrb.FragmentRealBarc
import com.arthuralexandryan.footballquiz.versus.vsrm.FragmentRonMessi

class VersusFragment : Fragment() {

    private var _binding: ActivityVersusBinding? = null
    private val binding get() = _binding!!
    
    private val versusList: List<Fragment> = listOf(FragmentRonMessi(), FragmentRealBarc())
    private var i = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityVersusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initFragments()
        onClicks()
        
        binding.onBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun onClicks() {
        binding.backward.setOnClickListener {
            if (i != 0) {
                i--
                updateFragment()
            }
        }
        binding.forward.setOnClickListener {
            if (i != versusList.size - 1) {
                i++
                updateFragment()
            }
        }
    }

    private fun updateFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.container, versusList[i])
            .commit()
            
        // Обновляем иконки стрелок
        binding.forward.setImageResource(if (i == versusList.size - 1) 0 else TarberakArrays.getBFicons()[i + 1])
        binding.backward.setImageResource(if (i == 0) 0 else TarberakArrays.getBFicons()[i - 1])
    }

    private fun initFragments() {
        childFragmentManager.beginTransaction()
            .replace(R.id.container, versusList[0])
            .commit()
        
        binding.forward.setImageResource(TarberakArrays.getBFicons()[1])
        binding.backward.setImageResource(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
