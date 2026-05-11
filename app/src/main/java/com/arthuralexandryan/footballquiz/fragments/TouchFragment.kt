package com.arthuralexandryan.footballquiz.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arthuralexandryan.footballquiz.databinding.ActivityTouchBinding

class TouchFragment : Fragment() {

    private var _binding: ActivityTouchBinding? = null
    private val binding get() = _binding!!

    private var setX = mutableListOf<Float>()
    private var setY = mutableListOf<Float>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityTouchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.setOnTouchListener { _, event ->
            handleTouch(event)
            true
        }
    }

    private fun handleTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("TouchFragment", "Touch Down")
                setX = mutableListOf()
                setY = mutableListOf()
            }
            MotionEvent.ACTION_MOVE -> {
                setX.add(event.x)
                setY.add(event.y)
            }
            MotionEvent.ACTION_UP -> {
                Log.e("TouchFragment", "Touch Up: $setX\n $setY")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
