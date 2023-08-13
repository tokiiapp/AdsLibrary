package com.toki.tokiapp.adslibrary.ads.callback

interface AppOpenSplashCallback {
    fun onAdFail(error: String)
    fun onAdClosed()
}