package com.arthuralexandryan.footballquiz.models

import com.arthuralexandryan.footballquiz.interfaces.ShowAds

class AdMobPresenter(private val showAds: ShowAds) {
    fun onShow(amount: Int) = showAds.show(amount)
}
