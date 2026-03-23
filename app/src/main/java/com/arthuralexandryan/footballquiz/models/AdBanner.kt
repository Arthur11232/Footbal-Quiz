package com.arthuralexandryan.footballquiz.models

import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class AdBanner(private val adView: AdView) {

    init {
        createBannerAdView()
    }

    private fun createBannerAdView() {
        val adRequest = AdRequest.Builder().build()
        adView.adListener = adListener
        adView.loadAd(adRequest)
    }

    fun stopLoading() = adView.pause()
    fun startLoading() = adView.resume()
    fun destroyLoading() = adView.destroy()

    private val adListener = object : AdListener() {
        override fun onAdLoaded() {
            Log.e("mAdView", "onAdLoaded")
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
            Log.e("mAdView", "onAdFailedToLoad: ${error.message}")
        }

        override fun onAdOpened() {
            Log.e("mAdView", "onAdOpened")
        }

        override fun onAdClosed() {
            Log.e("mAdView", "onAdClosed")
        }
        
        override fun onAdClicked() {
            Log.e("mAdView", "onAdClicked")
        }
    }
}
