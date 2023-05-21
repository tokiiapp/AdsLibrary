package com.toki.tokiapp.adslibrary.ads.model

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.interstitial.InterstitialAd

class InterHolderSimple (var adId: String) {
    var inter: InterstitialAd? = null
    val mutableLiveData: MutableLiveData<InterstitialAd> = MutableLiveData()
    var isLoaded = false
}