package com.arthuralexandryan.footballquiz.models

import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class AdBanner(private val adView: AdView) {

    init {
        createBannerAdView()
    }

    private fun createBannerAdView() {
        val adRequest = AdRequest.Builder().addTestDevice("5FB76465AA8B88DE6B66D105F23DF90D").build()
        adView.adListener = adListener
        adView.loadAd(adRequest)
    }

    fun stopLoading() = adView.pause()
    fun startLoading() = adView.resume()
    fun destroyLoading() = adView.destroy()

    private val adListener = object : AdListener() {
        override fun onAdLoaded() { Log.e("mAdView", "onAdLoaded") }
        override fun onAdFailedToLoad(errorCode: Int) { Log.e("mAdView", "onAdFailedToLoad") }
        override fun onAdOpened() { Log.e("mAdView", "onAdOpened") }
        override fun onAdLeftApplication() { Log.e("mAdView", "onAdLeftApplication") }
        override fun onAdClosed() { Log.e("mAdView", "onAdClosed") }
    }
}
