package com.arthuralexandryan.footballquiz.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.arthuralexandryan.footballquiz.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Включаем Edge-to-Edge для Android 15+
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
