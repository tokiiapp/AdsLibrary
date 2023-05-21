package com.toki.tokiapp.adslibrary.ads.callback

import com.google.android.gms.ads.interstitial.InterstitialAd

interface LoadInterCallBack {
    fun onAdClosed()
    fun onAdLoaded()
    fun onAdFail(error: String)
}