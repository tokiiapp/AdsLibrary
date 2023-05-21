package com.toki.tokiapp.adslibrary.ads.model

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.interstitial.InterstitialAd

class InterHolderMulti(var adId: String, var adId2: String) {
    var inter: InterstitialAd? = null
    var inter2: InterstitialAd? = null
    val mutableLiveData1: MutableLiveData<InterstitialAd> = MutableLiveData()
    val mutableLiveData2: MutableLiveData<InterstitialAd> = MutableLiveData()
    var isLoaded = false
}
