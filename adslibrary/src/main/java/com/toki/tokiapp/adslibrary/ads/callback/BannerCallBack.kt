package com.toki.tokiapp.adslibrary.ads.callback

import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.AdView

interface BannerCallBack {
    fun onLoad()
    fun onFailed()
    fun onPaid(adValue: AdValue?, mAdView: AdView?)

    fun onClosed(adSize: AdSize){}
}