package com.toki.tokiapp.adslibrary.ads.callback

import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.nativead.NativeAd

interface NativeAdCallback {
    fun onNativeAdLoaded()
    fun onAdFail()
    fun onAdPaid(adValue: AdValue?)
}