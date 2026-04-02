package com.arthuralexandryan.footballquiz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.databinding.ActivityTestBinding
import com.arthuralexandryan.footballquiz.models.PlaceModel
import com.arthuralexandryan.footballquiz.models.PlaceModelInterface

class TestFragment : Fragment(), PlaceModelInterface {

    private var _binding: ActivityTestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        PlaceModel.PlaceModelBuilder(binding.testImage.root)
            .setImage(R.drawable.barcelona_logo)
            .setVersusFirst("Barcelona")
            .setVersusSecond("Welcome!")
            .setListener(this)
            .build()
    }

    override fun onClickPlace() {
        findNavController().navigate(R.id.action_test_to_touch)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
