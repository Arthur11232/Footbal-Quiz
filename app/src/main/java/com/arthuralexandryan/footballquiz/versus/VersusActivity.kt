package com.arthuralexandryan.footballquiz.versus

import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.activities.BaseActivity
import com.arthuralexandryan.footballquiz.models.TarberakArrays
import com.arthuralexandryan.footballquiz.versus.vsrb.FragmentRealBarc
import com.arthuralexandryan.footballquiz.versus.vsrm.FragmentRonMessi

class VersusActivity : BaseActivity() {

    private lateinit var transaction: FragmentTransaction
    private lateinit var backward: AppCompatImageView
    private lateinit var forward: AppCompatImageView
    private val versusList: MutableList<Fragment> = mutableListOf()
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_versus)
        initViews()
        initFragments()
        onClicks()
    }

    private fun onClicks() {
        backward.setOnClickListener {
            if (i != 0) {
                i--
                transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container, versusList[i])
                transaction.commit()
                forward.setImageResource(TarberakArrays.getBFicons()[i + 1])
                backward.setImageResource(if (i == 0) 0 else TarberakArrays.getBFicons()[i])
            }
        }
        forward.setOnClickListener {
            if (i != versusList.size - 1) {
                i++
                transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container, versusList[i])
                transaction.commit()
                backward.setImageResource(TarberakArrays.getBFicons()[i - 1])
                forward.setImageResource(if (i == versusList.size) 0 else TarberakArrays.getBFicons()[i + 1])
            }
        }
    }

    private fun initFragments() {
        versusList.add(FragmentRonMessi())
        versusList.add(FragmentRealBarc())
        transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, versusList[0])
        transaction.commit()
    }

    private fun initViews() {
        backward = findViewById(R.id.backward)
        forward = findViewById(R.id.forward)
        forward.setImageResource(TarberakArrays.getBFicons()[1])
    }
}
