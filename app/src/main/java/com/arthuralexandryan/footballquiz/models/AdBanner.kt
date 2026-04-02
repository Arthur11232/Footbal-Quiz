package com.arthuralexandryan.footballquiz.models

import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import com.google.android.gms.ads.*

class AdBanner(private val activity: Activity, private val container: ViewGroup, private val adUnitId: String) {

    private var adView: AdView? = null

    init {
        loadBanner()
    }

    private fun loadBanner() {
        adView = AdView(activity)
        adView?.adUnitId = adUnitId
        adView?.setAdSize(getAdSize())

        adView?.adListener = adListener

        container.removeAllViews()
        container.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
    }

    private fun getAdSize(): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density
        val adWidthPixels = outMetrics.widthPixels.toFloat()
        val adWidth = (adWidthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    fun startLoading() = adView?.resume()
    fun stopLoading() = adView?.pause()
    fun destroyLoading() = adView?.destroy()

    private val adListener = object : AdListener() {
        override fun onAdLoaded() {
            Log.d("AdBanner", "onAdLoaded")
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
            Log.e("AdBanner", "onAdFailedToLoad: ${error.message}")
        }

        override fun onAdOpened() {
            Log.d("AdBanner", "onAdOpened")
        }

        override fun onAdClosed() {
            Log.d("AdBanner", "onAdClosed")
        }

        override fun onAdClicked() {
            Log.d("AdBanner", "onAdClicked")
        }
    }
}

